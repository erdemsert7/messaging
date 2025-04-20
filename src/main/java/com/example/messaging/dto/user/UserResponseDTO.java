package com.example.messaging.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;

    public UserResponseDTO(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}