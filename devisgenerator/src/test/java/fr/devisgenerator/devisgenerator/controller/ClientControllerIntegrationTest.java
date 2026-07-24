package fr.devisgenerator.devisgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.dto.request.RegisterRequest;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Test
    void createClientShouldReturn409WhenSameUserUsesSameEmail() throws Exception {

        String token = getToken();

        ClientRequest request = new ClientRequest(
                "ACME",
                "contact@acme.com",
                "0102030405",
                "Paris"
        );

        // Première création : OK
        mockMvc.perform(
                        post("/api/clients")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        // Deuxième création avec le même email
        mockMvc.perform(
                        post("/api/clients")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("Cet email est déjà utilisé pour ce client"));
    }

    @Test
    void createClientShouldAllowSameEmailForDifferentUsers() throws Exception {

        String tokenUser1 = getToken();
        String tokenUser2 = getToken();

        ClientRequest request = new ClientRequest(
                "ACME",
                "contact@acme.com",
                "0102030405",
                "Paris"
        );

        mockMvc.perform(
                        post("/api/clients")
                                .header("Authorization", "Bearer " + tokenUser1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/clients")
                                .header("Authorization", "Bearer " + tokenUser2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void findAllShouldReturnAllClientsForAdmin() throws Exception {

        AppUser user1 = createUser(
                "client-user-1",
                "password123",
                UserRole.ROLE_USER
        );

        AppUser user2 = createUser(
                "client-user-2",
                "password123",
                UserRole.ROLE_USER
        );

        createUser(
                "client-admin",
                "password123",
                UserRole.ROLE_ADMIN
        );

        createClient(
                "Alice",
                "alice@test.fr",
                user1
        );

        createClient(
                "Bob",
                "bob@test.fr",
                user2
        );

        String token = loginAndGetToken(
                "client-admin",
                "password123"
        );

        mockMvc.perform(get("/api/clients")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Alice")))
                .andExpect(content().string(containsString("Bob")));
    }

    @Test
    void findAllShouldReturnOnlyOwnedClientsForUser() throws Exception {

        AppUser user1 = createUser(
                "client-owner",
                "password123",
                UserRole.ROLE_USER
        );

        AppUser user2 = createUser(
                "client-other",
                "password123",
                UserRole.ROLE_USER
        );

        createClient(
                "Alice",
                "alice@test.fr",
                user1
        );

        createClient(
                "Bob",
                "bob@test.fr",
                user2
        );

        String token = loginAndGetToken(
                "client-owner",
                "password123"
        );

        mockMvc.perform(get("/api/clients")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    private String getToken() throws Exception {

        String username = "user-" + UUID.randomUUID();

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

    private AppUser createUser(
            String username,
            String password,
            UserRole role) {

        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role.name())
                .build();

        return userRepository.save(user);
    }

    private String loginAndGetToken(
            String username,
            String password
    ) throws Exception {

        LoginRequest request =
                new LoginRequest(username, password);

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

    private Client createClient(
            String name,
            String email,
            AppUser owner
    ) {

        Client client = Client.builder()
                .name(name)
                .email(email)
                .phone("0102030405")
                .address("1 rue des test")
                .user(owner)
                .build();

        return clientRepository.save(client);
    }

}
