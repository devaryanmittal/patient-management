package com.pm.analytics_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pm.analytics_service.model.DashboardMetrics;

@Repository
public interface DashboardMetricsRepository extends JpaRepository<DashboardMetrics, Long> {
}