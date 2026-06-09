package fr.devisgenerator.devisgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createClientShouldReturn201() throws Exception {

        String token = getToken();

        mockMvc.perform(
                        post("/api/clients")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                validClientRequest()
                                        )
                                )
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createClientShouldReturn400WhenNameIsBlank() throws Exception {

        String token = getToken();

        ClientRequest request =
                new ClientRequest(
                        "",
                        "contact@acme.com",
                        "0102030405",
                        "Paris"
                );

        mockMvc.perform(
                post("/api/clients")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllShouldReturn200() throws Exception {

        String token = getToken();

        mockMvc.perform(
                        get("/api/clients")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void findAllShouldReturn401WithoutToken() throws Exception {

        mockMvc.perform(
                get("/api/clients")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByIdShouldReturn200WhenClientExists()
            throws Exception {

        String token = getToken();

        Long clientId = createClient(
                token,
                validClientRequest()
        );

        mockMvc.perform(
                        get("/api/clients/" + clientId)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturn404WhenClientDoesNotExist()
            throws Exception {

        String token = getToken();

        mockMvc.perform(
                        get("/api/clients/999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn200()
            throws Exception {

        String token = getToken();

        Long clientId = createClient(
                token,
                validClientRequest()
        );

        ClientRequest updateRequest =
                new ClientRequest(
                        "ACME Updated",
                        "new@acme.com",
                        "0606060606",
                        "Lyon"
                );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                                        "/api/clients/" + clientId
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                updateRequest
                                        )
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteShouldReturn204()
            throws Exception {

        String token = getToken();

        Long clientId = createClient(
                token,
                validClientRequest()
        );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                                        "/api/clients/" + clientId
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());
    }

    private String getToken() throws Exception {

        String username =
                "user" + System.currentTimeMillis();

        RegisterRequest registerRequest =
                new RegisterRequest(
                        username,
                        "password123"
                );

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        registerRequest
                                )
                        )
        );

        LoginRequest loginRequest =
                new LoginRequest(
                        username,
                        "password123"
                );

        String response =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        loginRequest
                                                )
                                        )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper
                .readTree(response)
                .get("token")
                .asText();
    }

    private Long createClient(
            String token,
            ClientRequest request
    ) throws Exception {

        String response =
                mockMvc.perform(
                                post("/api/clients")
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        request
                                                )
                                        )
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper
                .readTree(response)
                .get("id")
                .asLong();
    }

    private ClientRequest validClientRequest() {

        String unique = UUID.randomUUID().toString();

        return new ClientRequest(
                "ACME " + unique,
                unique +  "@acme.com",
                "0102030405",
                "Paris"
        );
    }

}
