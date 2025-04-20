package com.example.messaging.controller;

import com.example.messaging.dto.user.UserRequestDTO;
import com.example.messaging.dto.user.UserResponseDTO;
import com.example.messaging.entity.User;
import com.example.messaging.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("testuser");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setPassword("password123");

        userResponseDTO = new UserResponseDTO("507f1f77bcf86cd799439011", "testuser", "test@example.com");
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO();
        invalidDTO.setUsername("");
        invalidDTO.setEmail("invalid");
        invalidDTO.setPassword("");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindUserByUsername() throws Exception {
        User user = new User("1", "testuser", "test@example.com", "encodedPassword");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users?username=testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenUsernameNotFound() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users?username=testuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameMissing() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindUserByEmail() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(userResponseDTO));

        mockMvc.perform(get("/api/users?email=test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenEmailNotFound() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users?email=test@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenEmailMissing() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindUserById() throws Exception {
        when(userService.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(new User("507f1f77bcf86cd799439011", "testuser", "test@example.com", "encodedPassword")));

        mockMvc.perform(get("/api/users/507f1f77bcf86cd799439011"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("507f1f77bcf86cd799439011"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenIdNotFound() throws Exception {
        when(userService.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/507f1f77bcf86cd799439011"))
                .andExpect(status().isNotFound());
    }
}