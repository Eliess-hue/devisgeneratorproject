package fr.devisgenerator.devisgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.dto.request.RegisterRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerShouldReturn200WhenValidRequest() throws Exception {

        RegisterRequest request =
                new RegisterRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {

        // Arrange
        RegisterRequest registerRequest =
                new RegisterRequest(
                        "loginuser",
                        "password123"
                );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(registerRequest)
                        ))
                .andExpect(status().isOk());

        LoginRequest loginRequest =
                new LoginRequest(
                        "loginuser",
                        "password123"
                );

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(loginRequest)
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void registerShouldReturn409WhenUsernameAlreadyExists() throws Exception {

        RegisterRequest request =
                new RegisterRequest(
                        "existinguser",
                        "password123"
                );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        ))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        ))
                .andExpect(status().isConflict());
    }

    @Test
    void registerShouldReturn400WhenUsernameIsBlank() throws Exception {

        RegisterRequest request =
                new RegisterRequest(
                        "",
                        "password123"
                );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username is required")));
    }

}
