package com.example.messaging.controller;

import com.example.messaging.dto.message.MessageRequestDTO;
import com.example.messaging.dto.message.MessageResponseDTO;
import com.example.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestBody MessageRequestDTO request, Authentication authentication) {
        String senderUsername = authentication.getName();
        MessageResponseDTO response = messageService.sendMessage(senderUsername, request.getReceiverUsername(), request.getContent());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<MessageResponseDTO>> getMessageHistory(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        List<MessageResponseDTO> messages = messageService.getMessageHistory(currentUsername, username);
        return ResponseEntity.ok(messages);
    }
}