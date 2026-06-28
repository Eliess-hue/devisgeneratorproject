package fr.devisgenerator.devisgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.devisgenerator.devisgenerator.dto.request.*;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    void findAllQuotesShouldReturn200()
            throws Exception {

        String token = getToken();

        mockMvc.perform(
                        get("/api/quotes")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void findAllQuotesShouldReturn401WithoutToken()
            throws Exception {

        mockMvc.perform(
                        get("/api/quotes")
                )
                .andExpect(status().isUnauthorized());
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
                        BigDecimal.valueOf(450)
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
                        BigDecimal.valueOf(500)
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
                        BigDecimal.valueOf(450)
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
                .andExpect(jsonPath("$.lines.length()").value(1));
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

}
