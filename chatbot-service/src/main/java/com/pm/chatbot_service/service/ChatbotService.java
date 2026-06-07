package com.pm.chatbot_service.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

        private final ChatClient chatClient;

        public ChatbotService(ChatClient.Builder chatClientBuilder) {
                ChatMemory chatMemory = MessageWindowChatMemory.builder()
                                .maxMessages(20) // Limits memory to the last 20 messages to save context window tokens
                                .build();

                this.chatClient = chatClientBuilder
                                .defaultSystem("You are a helpful, polite healthcare assistant for a Patient Management System. "
                                                +
                                                "If a user asks about their bills or appointments, use the provided tools to fetch real-time data.")
                                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                // Explicitly register the names of the @Beans created in AiToolsConfig
                                .defaultTools("getPatientBilling", "getPatientAppointments")
                                .build();
        }

        public String chat(String patientId, String userMessage) {
                return this.chatClient.prompt()
                                .user(userMessage)
                                // Use patientId as the memory key so each patient has isolated chat history
                                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, patientId))
                                .call()
                                .content();
        }
}