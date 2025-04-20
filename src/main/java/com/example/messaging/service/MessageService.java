package com.example.messaging.service;

import com.example.messaging.dto.message.MessageResponseDTO;
import com.example.messaging.entity.Message;
import com.example.messaging.exception.ForbiddenException;
import com.example.messaging.exception.NotFoundException;
import com.example.messaging.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserService userService){
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public MessageResponseDTO sendMessage(String senderUsername, String receiverUsername, String content) {
        checkUser(senderUsername,receiverUsername,true);
        Message message = new Message();
        message.setSenderUsername(senderUsername);
        message.setReceiverUsername(receiverUsername);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);

        return new MessageResponseDTO(senderUsername, receiverUsername, content, message.getTimestamp());
    }

    public List<MessageResponseDTO> getMessageHistory(String currentUsername, String otherUsername) {
        checkUser(currentUsername,otherUsername,false);
        List<Message> messages = messageRepository.findBySenderAndReceiver(currentUsername, otherUsername);
        return messages.stream()
                .map(msg -> new MessageResponseDTO(msg.getSenderUsername(), msg.getReceiverUsername(), msg.getContent(), msg.getTimestamp()))
                .collect(Collectors.toList());
    }

    public void checkUser(String currentUsername, String otherUsername, Boolean isSendMessages) {
        if (userService.findByUsername(otherUsername).isEmpty()) {
            throw new NotFoundException("User '" + otherUsername + "' does not exist");
        }

        if (currentUsername.equals(otherUsername)) {
            throw new ForbiddenException(
                    isSendMessages ? "Cannot send message to self" : "Cannot view message history with self"
            );
        }
    }
}