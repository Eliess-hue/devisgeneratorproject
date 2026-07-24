package fr.devisgenerator.devisgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.devisgenerator.devisgenerator.dto.request.ChangeUserRoleRequest;
import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void findAllShouldReturn401WithoutJwt() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAllShouldReturn403ForUser() throws Exception {

        createUser(
                "user",
                "password123",
                UserRole.ROLE_USER
        );

        String token = loginAndGetToken(
                "user",
                "password123"
        );

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAllShouldReturn200ForAdmin() throws Exception {

        createUser(
                "august",
                "admin123",
                UserRole.ROLE_ADMIN
        );

        String token = loginAndGetToken(
                "august",
                "admin123"
        );

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].role").exists());
    }

    @Test
    void changeRoleShouldReturn401WithoutJwt() throws Exception {

        AppUser user = createUser(
                "user",
                "password123",
                UserRole.ROLE_USER
        );

        ChangeUserRoleRequest request = new ChangeUserRoleRequest(UserRole.ROLE_ADMIN);

        mockMvc.perform(
                        put("/api/users/{id}/role", user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeRoleShouldReturn403ForUser() throws Exception {

        AppUser target = createUser(
                "target",
                "password123",
                UserRole.ROLE_USER
        );

        createUser(
                "user",
                "password123",
                UserRole.ROLE_USER
        );

        String token = loginAndGetToken(
                "user",
                "password123"
        );

        ChangeUserRoleRequest request = new ChangeUserRoleRequest(UserRole.ROLE_ADMIN);

        mockMvc.perform(
                        put("/api/users/{id}/role", target.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeRoleShouldReturn200ForAdmin() throws Exception {

        AppUser target = createUser(
                "target",
                "password123",
                UserRole.ROLE_USER
        );

        createUser(
                "august",
                "admin123",
                UserRole.ROLE_ADMIN
        );

        String token = loginAndGetToken(
                "august",
                "admin123"
        );

        ChangeUserRoleRequest request = new ChangeUserRoleRequest(UserRole.ROLE_ADMIN);

        mockMvc.perform(
                        put("/api/users/{id}/role", target.getId())
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(target.getId()))
                .andExpect(jsonPath("$.username").value("target"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));

        AppUser updated = appUserRepository.findById(target.getId()).orElseThrow();

        assertEquals(
                UserRole.ROLE_ADMIN.name(),
                updated.getRole()
        );
    }

    @Test
    void changeRoleShouldReturn404WhenUserDoesNotExist() throws Exception {

        createUser(
                "august",
                "admin123",
                UserRole.ROLE_ADMIN
        );

        String token = loginAndGetToken(
                "august",
                "admin123"
        );

        ChangeUserRoleRequest request = new ChangeUserRoleRequest(UserRole.ROLE_ADMIN);

        mockMvc.perform(
                        put("/api/users/{id}/role", 99999L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    private AppUser createUser(String username, String password, UserRole role) {

        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role.name())
                .build();

        return appUserRepository.save(user);
    }

    private String loginAndGetToken(String username, String password) throws Exception {

        LoginRequest request = new LoginRequest(username, password);

        String response =
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper.readTree(response)
                .get("token")
                .asText();
    }
}