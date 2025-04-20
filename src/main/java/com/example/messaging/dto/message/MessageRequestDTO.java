package com.example.messaging.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDTO {
    @NotBlank(message = "Receiver username is required")
    private String receiverUsername;

    @NotBlank(message = "Content is required")
    private String content;
}