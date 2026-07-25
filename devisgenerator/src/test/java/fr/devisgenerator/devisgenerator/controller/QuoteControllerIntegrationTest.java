package fr.devisgenerator.devisgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.devisgenerator.devisgenerator.dto.request.*;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class QuoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuoteLineRepository quoteLineRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createQuoteShouldReturn201() throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        QuoteRequest request =
                new QuoteRequest(
                        clientId,
                        QuoteStatus.DRAFT
                );

        mockMvc.perform(
                        post("/api/quotes")
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
                .andExpect(status().isCreated());
    }

    @Test
    void findQuoteByIdShouldReturn200()
            throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        mockMvc.perform(
                        get("/api/quotes/" + quoteId)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void findQuoteByIdShouldReturn404()
            throws Exception {

        String token = getToken();

        mockMvc.perform(
                        get("/api/quotes/999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void addLineShouldReturn201()
            throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        QuoteLineRequest request =
                new QuoteLineRequest(
                        "Développement Docker",
                        2,
                        BigDecimal.valueOf(450),
                        BigDecimal.valueOf(0.20)
                );

        mockMvc.perform(
                        post("/api/quotes/" + quoteId + "/lines")
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
                .andExpect(status().isCreated());
    }

    @Test
    void updateLineShouldReturn200()
            throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        Long lineId =
                createQuoteLine(
                        token,
                        quoteId
                );

        QuoteLineRequest request =
                new QuoteLineRequest(
                        "Développement Spring Boot",
                        3,
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(0.10)
                );

        mockMvc.perform(
                        put(
                                "/api/quotes/"
                                        + quoteId
                                        + "/lines/"
                                        + lineId
                        )
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
                .andExpect(status().isOk());
    }

    @Test
    void deleteLineShouldReturn200()
            throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        Long lineId =
                createQuoteLine(
                        token,
                        quoteId
                );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                                        "/api/quotes/"
                                                + quoteId
                                                + "/lines/"
                                                + lineId
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void deleteQuoteShouldReturn204()
            throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        Long lineId =
                createQuoteLine(
                        token,
                        quoteId
                );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                                        "/api/quotes/"
                                                + quoteId
                                                + "/lines/"
                                                + lineId
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                                        "/api/quotes/" + quoteId
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

    private Long createQuote(
            String token,
            Long clientId
    ) throws Exception {

        QuoteRequest request =
                new QuoteRequest(
                        clientId,
                        QuoteStatus.DRAFT
                );

        String response =
                mockMvc.perform(
                                post("/api/quotes")
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

    private Long createQuoteLine(
            String token,
            Long quoteId
    ) throws Exception {

        QuoteLineRequest request =
                new QuoteLineRequest(
                        "Développement Docker",
                        2,
                        BigDecimal.valueOf(450),
                        BigDecimal.valueOf(0.20)
                );

        mockMvc.perform(
                        post("/api/quotes/" + quoteId + "/lines")
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
                .andExpect(status().isCreated());

        return quoteLineRepository
                .findByQuote_Id(quoteId)
                .stream()
                .map(line -> line.getId())
                .max(Long::compareTo)
                .orElseThrow();
    }

    @Test
    void duplicateQuoteShouldReturn201() throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        createQuoteLine(token, quoteId);

        mockMvc.perform(
                        post("/api/quotes/" + quoteId + "/duplicate")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isCreated())

                // Le nouveau devis est en brouillon
                .andExpect(jsonPath("$.status").value("DRAFT"))

                // Le client est conservé
                .andExpect(jsonPath("$.client.id").value(clientId))

                // Un nouveau numéro est généré
                .andExpect(jsonPath("$.number").isNotEmpty())

                // Les lignes ont bien été dupliquées
                .andExpect(jsonPath("$.lines.length()").value(1))

                // Lignes + tous les totaux
                .andExpect(jsonPath("$.lines[0].vatRate").value(0.20))
                .andExpect(jsonPath("$.totalHt").value(900))
                .andExpect(jsonPath("$.totalTva").value(180.0))
                .andExpect(jsonPath("$.totalTtc").value(1080.0));
    }

    @Test
    void searchQuotesShouldReturn200() throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        createQuote(
                token,
                clientId
        );

        mockMvc.perform(
                        get("/api/quotes/search")
                                .param("status", "DRAFT")
                                .param("page", "0")
                                .param("size", "10")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.content[0].client.id").value(clientId))
                .andExpect(jsonPath("$.content[0].number").exists())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void searchQuotesShouldReturn401WithoutToken()
            throws Exception {

        mockMvc.perform(
                        get("/api/quotes/search")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generatePdfShouldReturn200AndPdf()
            throws Exception {

        String token = getToken();

        Long clientId =
                createClient(
                        token,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        token,
                        clientId
                );

        createQuoteLine(
                token,
                quoteId
        );

        byte[] pdf =
                mockMvc.perform(
                                get("/api/quotes/" + quoteId + "/pdf")
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andExpect(
                                content()
                                        .contentType(MediaType.APPLICATION_PDF)
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsByteArray();


        assertThat(pdf.length)
                .isGreaterThan(0);
    }

    @Test
    void generatePdfShouldReturn403WhenQuoteDoesNotBelongToUser()
            throws Exception {

        String ownerToken = getToken();

        Long clientId =
                createClient(
                        ownerToken,
                        validClientRequest()
                );

        Long quoteId =
                createQuote(
                        ownerToken,
                        clientId
                );


        String otherToken = getToken();


        mockMvc.perform(
                        get("/api/quotes/" + quoteId + "/pdf")
                                .header(
                                        "Authorization",
                                        "Bearer " + otherToken
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void generatePdfShouldReturn404WhenQuoteDoesNotExist()
            throws Exception {

        String token = getToken();

        mockMvc.perform(
                        get("/api/quotes/999999/pdf")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void searchShouldReturnAllQuotesForAdmin() throws Exception {

        AppUser user1 = createUser(
                "quote-user-1",
                "password123",
                UserRole.ROLE_USER
        );

        AppUser user2 = createUser(
                "quote-user-2",
                "password123",
                UserRole.ROLE_USER
        );

        createUser(
                "quote-admin",
                "password123",
                UserRole.ROLE_ADMIN
        );

        Client client1 = createClient(
                "Alice",
                "alice@test.fr",
                user1
        );

        Client client2 = createClient(
                "Bob",
                "bob@test.fr",
                user2
        );

        createQuote(
                "DEV-2025-001",
                client1,
                user1
        );

        createQuote(
                "DEV-2025-002",
                client2,
                user2
        );

        String token = loginAndGetToken(
                "quote-admin",
                "password123"
        );

        mockMvc.perform(
                        get("/api/quotes/search")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "0")
                                .param("size", "50")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("DEV-2025-001")))
                .andExpect(content().string(containsString("DEV-2025-002")));
    }

    @Test
    void searchShouldReturnOnlyOwnedQuotesForUser() throws Exception {

        AppUser user1 = createUser(
                "quote-owner",
                "password123",
                UserRole.ROLE_USER
        );

        AppUser user2 = createUser(
                "quote-other",
                "password123",
                UserRole.ROLE_USER
        );

        Client client1 = createClient(
                "Alice",
                "alice@test.fr",
                user1
        );

        Client client2 = createClient(
                "Bob",
                "bob@test.fr",
                user2
        );

        createQuote(
                "DEV-2025-001",
                client1,
                user1
        );

        createQuote(
                "DEV-2025-002",
                client2,
                user2
        );

        String token = loginAndGetToken(
                "quote-owner",
                "password123"
        );

        mockMvc.perform(
                        get("/api/quotes/search")
                                .header("Authorization", "Bearer " + token)
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].number")
                        .value("DEV-2025-001"));
    }

    private AppUser createUser(
            String username,
            String password,
            UserRole role) {

        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
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
                .address("1 rue des Test")
                .user(owner)
                .build();

        return clientRepository.save(client);
    }

    private Quote createQuote(
            String number,
            Client client,
            AppUser owner
    ) {

        Quote quote = Quote.builder()
                .number(number)
                .status(QuoteStatus.DRAFT)
                .client(client)
                .user(owner)
                .totalHt(BigDecimal.ZERO)
                .totalTva(BigDecimal.ZERO)
                .totalTtc(BigDecimal.ZERO)
                .build();

        return quoteRepository.save(quote);
    }

}
