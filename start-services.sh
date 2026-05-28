#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Patient Management Microservices Setup     ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}\n"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is installed${NC}"

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}✗ Docker Compose is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker Compose is installed${NC}\n"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}⚠ Maven is not installed (optional, but recommended)${NC}\n"
else
    echo -e "${GREEN}✓ Maven is installed${NC}\n"
fi

echo -e "${BLUE}════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}Building Docker images...${NC}"
echo -e "${BLUE}════════════════════════════════════════════════${NC}\n"

# Build images
docker-compose build

if [ $? -ne 0 ]; then
    echo -e "\n${RED}✗ Docker build failed${NC}"
    exit 1
fi

echo -e "\n${GREEN}✓ Docker images built successfully${NC}\n"

echo -e "${BLUE}════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}Starting services...${NC}"
echo -e "${BLUE}════════════════════════════════════════════════${NC}\n"

# Start services
docker-compose up -d

if [ $? -ne 0 ]; then
    echo -e "\n${RED}✗ Failed to start services${NC}"
    exit 1
fi

echo -e "\n${GREEN}✓ Services started${NC}"
echo -e "${YELLOW}Waiting for services to be healthy...${NC}\n"

# Wait for services to be ready
max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
    # Check if all services are running
    eureka_status=$(docker ps | grep eureka-server | wc -l)
    billing_status=$(docker ps | grep billing-service | wc -l)
    patient_status=$(docker ps | grep patient-service | wc -l)
    
    if [ $eureka_status -eq 1 ] && [ $billing_status -eq 1 ] && [ $patient_status -eq 1 ]; then
        # Check health endpoints
        eureka_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8761/eureka/apps 2>/dev/null)
        billing_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:4001/actuator/health 2>/dev/null)
        patient_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:4000/actuator/health 2>/dev/null)
        
        if [ "$eureka_health" = "200" ] && [ "$billing_health" = "200" ] && [ "$patient_health" = "200" ]; then
            echo -e "${GREEN}✓ All services are healthy${NC}\n"
            break
        fi
    fi
    
    echo "Checking service health... (attempt $((attempt+1))/$max_attempts)"
    sleep 2
    attempt=$((attempt+1))
done

echo -e "${BLUE}════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✓ Setup Complete!${NC}"
echo -e "${BLUE}════════════════════════════════════════════════${NC}\n"

echo -e "${YELLOW}📌 Service URLs:${NC}"
echo -e "  Eureka Server:    ${GREEN}http://localhost:8761${NC}"
echo -e "  Billing Service:  ${GREEN}http://localhost:4001${NC}"
echo -e "  Patient Service:  ${GREEN}http://localhost:4000${NC}\n"

echo -e "${YELLOW}🐘 Database Connections:${NC}"
echo -e "  Patient DB: ${GREEN}localhost:5432${NC} (user: patient_user)"
echo -e "  Billing DB: ${GREEN}localhost:5433${NC} (user: billing_user)\n"

echo -e "${YELLOW}📚 Useful Commands:${NC}"
echo -e "  View logs:        ${GREEN}docker-compose logs -f <service-name>${NC}"
echo -e "  Stop services:    ${GREEN}docker-compose down${NC}"
echo -e "  Stop & clean:     ${GREEN}docker-compose down -v${NC}"
echo -e "  Service status:   ${GREEN}docker-compose ps${NC}\n"

echo -e "${BLUE}════════════════════════════════════════════════${NC}"
echo -e "For more details, see ${YELLOW}DOCKER_SETUP.md${NC}"
echo -e "${BLUE}════════════════════════════════════════════════${NC}\n"
