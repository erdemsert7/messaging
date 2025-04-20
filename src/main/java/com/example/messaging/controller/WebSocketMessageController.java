package com.example.messaging.controller;

import com.example.messaging.config.JwtUtil;
import com.example.messaging.dto.message.MessageRequestDTO;
import com.example.messaging.dto.message.MessageResponseDTO;
import com.example.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController {

    private final MessageService messageService;

    @Autowired
    public WebSocketMessageController(MessageService messageService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.messageService = messageService;
    }

    @MessageMapping("/send/{receiverUsername}")
    @SendTo("/topic/messages/{receiverUsername}")
    public MessageResponseDTO sendMessage(@DestinationVariable String receiverUsername,
                                          MessageRequestDTO request,
                                          Authentication authentication) {
        String senderUsername = authentication.getName();
        return messageService.sendMessage(senderUsername, receiverUsername, request.getContent());
    }
}