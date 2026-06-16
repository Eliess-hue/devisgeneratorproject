package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.request.QuoteRequest;
import fr.devisgenerator.devisgenerator.dto.response.QuoteResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class QuoteConcurrencyIntegrationTest {

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private QuoteLineRepository quoteLineRepository;

    private AppUser user;
    private Client client;
    private final List<Exception> errors =
            Collections.synchronizedList(
                    new ArrayList<>()
            );

    @BeforeEach
    void setUp() {

        errors.clear();

        cleanUp();

        user = appUserRepository.save(
                AppUser.builder()
                        .username("concurrency-user")
                        .password("password")
                        .role("ROLE_USER")
                        .build()
        );

        client = clientRepository.save(
                Client.builder()
                        .name("ACME")
                        .email("acme@test.com")
                        .user(user)
                        .build()
        );

        quoteService.create(
                new QuoteRequest(
                        client.getId(),
                        QuoteStatus.DRAFT
                ),
                user
        );
    }

    @AfterEach
    void cleanUp() {

        quoteLineRepository.deleteAll();
        quoteRepository.deleteAll();
        clientRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    /**
     * Test exploratoire de concurrence.
     *
     * Ce test met en évidence une limite de l'approche basée sur
     * PESSIMISTIC_WRITE dans generateQuoteNumber().
     *
     * Lorsque plusieurs threads exécutent create() simultanément,
     * ils peuvent tous verrouiller la même ligne représentant le
     * dernier devis connu au moment de la lecture.
     *
     * Après libération du verrou, PostgreSQL ne réévalue pas la
     * requête findTopByOrderByIdDesc(), ce qui peut conduire à la
     * génération de numéros identiques sous forte concurrence.
     *
     * Ce test a été créé à des fins pédagogiques pour documenter
     * le comportement observé. Il n'est pas destiné à être intégré
     * dans la suite de tests de production.
     */

    @Test
    void shouldGenerateUniqueQuoteNumbersWhenCreatingQuotesConcurrently()
        throws Exception {

        int threadCount = 10;

        Set<String> quoteNumbers =
                ConcurrentHashMap.newKeySet();

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        CountDownLatch doneSignal =
                new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {

            executor.submit(() -> {

                try {

                    startSignal.await();

                    QuoteRequest request =
                            new QuoteRequest(
                                    client.getId(),
                                    QuoteStatus.DRAFT
                            );

                    QuoteResponse response =
                            quoteService.create(
                                    request,
                                    user
                            );

                    quoteNumbers.add(
                            response.number()
                    );

                } catch (Exception e) {

                    errors.add(e);

                } finally {
                    doneSignal.countDown();
                }
            });
        }

        try {

            startSignal.countDown();
            doneSignal.await();

            assertTrue(
                    errors.isEmpty(),
                    "Des threads ont échoué : " + errors
            );

            //System.out.println("Numéros générés : " + quoteNumbers);

            assertEquals(
                    threadCount,
                    quoteNumbers.size()
            );

        } finally {

            executor.shutdown();

        }

    }

}
