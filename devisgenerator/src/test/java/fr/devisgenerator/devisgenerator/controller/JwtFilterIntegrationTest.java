package fr.devisgenerator.devisgenerator.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.crypto.SecretKey;
import java.util.Date;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class JwtFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401WhenTokenIsMalformed() throws Exception {

        mockMvc.perform(
                        get("/api/quotes")
                                .header(
                                        "Authorization",
                                        "Bearer ceci-n-est-pas-un-token-jwt"
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenSignatureIsInvalid() throws Exception {

        String invalidSignatureToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + 60_000
                        )
                )
                .signWith(
                        Jwts.SIG.HS256.key().build()
                )
                .compact();

        mockMvc.perform(
                        get("/api/quotes")
                                .header(
                                        "Authorization",
                                        "Bearer " + invalidSignatureToken
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenTokenIsExpired() throws Exception {

        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(
                        new Date(
                                System.currentTimeMillis() - 120_000
                        )
                )
                .expiration(
                        new Date(
                                System.currentTimeMillis() - 60_000
                        )
                )
                .signWith(getAppSigningKey())
                .compact();

        mockMvc.perform(
                        get("/api/quotes")
                                .header(
                                        "Authorization",
                                        "Bearer " + expiredToken
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getAppSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}