package com.pm.chatbot_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.chatbot_service.service.ChatbotService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/{patientId}")
    public ResponseEntity<String> chatWithPatient(
            @PathVariable String patientId,
            @RequestBody String message) {

        String aiResponse = chatbotService.chat(patientId, message);
        return ResponseEntity.ok(aiResponse);
    }
}