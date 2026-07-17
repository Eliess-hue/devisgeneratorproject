package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.QuoteLineRequest;
import fr.devisgenerator.devisgenerator.dto.request.QuoteRequest;
import fr.devisgenerator.devisgenerator.dto.response.QuoteResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.entity.QuoteLine;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.exception.QuoteNotFoundException;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import fr.devisgenerator.devisgenerator.dto.request.QuoteFilterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceImplTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private QuoteLineRepository quoteLineRepository;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    @Test
    void createShouldCreateQuoteSuccessfully() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .username("john")
                .role("ROLE_USER")
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(user)
                .build();

        QuoteRequest request =
                new QuoteRequest(1L, QuoteStatus.DRAFT);

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(quoteRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.empty());

        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        QuoteResponse response =
                quoteService.create(request, user);

        // Assert
        assertEquals("DEV-" + LocalDate.now().getYear() + "-001",
                response.number());

        assertEquals(QuoteStatus.DRAFT, response.status());

        verify(quoteRepository).save(any(Quote.class));
    }

    @Test
    void findByIdShouldReturnQuoteWhenFound() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .number("DEV-2024-001")
                .status(QuoteStatus.DRAFT)
                .client(client)
                .user(user)
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));

        QuoteResponse response =
                quoteService.findById(1L, user);

        assertEquals(1L, response.id());
    }

    @Test
    void findByIdShouldThrowQuoteNotFoundException() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                QuoteNotFoundException.class,
                () -> quoteService.findById(1L, user)
        );
    }

    @Test
    void findByIdShouldThrowAccessDeniedException() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        AppUser otherUser = AppUser.builder()
                .id(2L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(otherUser)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .client(client)
                .user(otherUser)
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));

        assertThrows(
                AccessDeniedException.class,
                () -> quoteService.findById(1L, user)
        );
    }

    @Test
    void deleteShouldDeleteQuoteSuccessfully() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .client(client)
                .user(user)
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));

        quoteService.delete(1L, user);

        verify(quoteRepository)
                .delete(quote);
    }

    @Test
    void addLineShouldAddLineAndRecalculateTotals() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .client(client)
                .user(user)
                .build();

        QuoteLineRequest request =
                new QuoteLineRequest(
                        "Développement",
                        2,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(0.20)
                );

        QuoteLine line = QuoteLine.builder()
                .quote(quote)
                .description("Développement")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .vatRate(BigDecimal.valueOf(0.20))
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));

        when(quoteLineRepository.findByQuote_Id(1L))
                .thenReturn(List.of(line));

        // Act
        QuoteResponse response =
                quoteService.addLine(1L, request, user);

        // Assert
        verify(quoteLineRepository)
                .save(any(QuoteLine.class));

        verify(quoteRepository)
                .save(quote);

        assertEquals(
                BigDecimal.valueOf(200),
                response.totalHt()
        );
    }

    @Test
    void updateLineShouldUpdateLineAndRecalculateTotals() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .client(client)
                .user(user)
                .build();

        QuoteLine line = QuoteLine.builder()
                .id(1L)
                .quote(quote)
                .description("Ancienne prestation")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .vatRate(BigDecimal.valueOf(0.20))
                .build();

        QuoteLineRequest request =
                new QuoteLineRequest(
                        "Nouvelle prestation",
                        3,
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(0.10)
                );

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));

        when(quoteLineRepository.findById(1L))
                .thenReturn(Optional.of(line));

        when(quoteLineRepository.findByQuote_Id(1L))
                .thenReturn(List.of(line));

        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        QuoteResponse response =
                quoteService.updateLine(
                        1L,
                        1L,
                        request,
                        user
                );

        // Assert
        verify(quoteLineRepository)
                .save(line);

        verify(quoteRepository)
                .save(quote);

        assertAll(
                () -> assertEquals(
                        "Nouvelle prestation",
                        line.getDescription()
                ),
                () -> assertEquals(
                        3,
                        line.getQuantity()
                ),
                () -> assertEquals(
                        BigDecimal.valueOf(200),
                        line.getUnitPrice()
                ),
                () -> assertEquals(
                        0,
                        response.totalHt()
                                .compareTo(BigDecimal.valueOf(600))
                )
        );
    }

    @Test
    void deleteLineShouldDeleteLineAndRecalculateTotals() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .client(client)
                .user(user)
                .build();

        QuoteLine line = QuoteLine.builder()
                .id(1L)
                .quote(quote)
                .description("Développement")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .vatRate(BigDecimal.valueOf(0.20))
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));

        when(quoteLineRepository.findById(1L))
                .thenReturn(Optional.of(line));

        // Après suppression il ne reste plus aucune ligne
        when(quoteLineRepository.findByQuote_Id(1L))
                .thenReturn(List.of());

        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        QuoteResponse response =
                quoteService.deleteLine(1L, 1L, user);

        // Assert
        verify(quoteLineRepository)
                .delete(line);

        verify(quoteRepository)
                .save(quote);

        assertAll(
                () -> assertEquals(
                        0,
                        response.totalHt().compareTo(BigDecimal.ZERO),
                        "totalHt should be zero"),
                () -> assertEquals(
                        0,
                        response.totalTva().compareTo(BigDecimal.ZERO),
                        "totalTva should be zero"),
                () -> assertEquals(
                        0,
                        response.totalTtc().compareTo(BigDecimal.ZERO),
                        "totalTtc should be zero")
        );
    }

    @Test
    void createShouldResetCounterWhenLastQuoteIsFromPreviousYear() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .username("john")
                .role("ROLE_USER")
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(user)
                .build();

        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        Quote lastQuote = Quote.builder()
                .id(50L)
                .number("DEV-" + previousYear + "-050")
                .build();

        QuoteRequest request =
                new QuoteRequest(
                        1L,
                        QuoteStatus.DRAFT
                );

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(quoteRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.of(lastQuote));

        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        QuoteResponse response =
                quoteService.create(request, user);

        // Assert
        assertEquals(
                "DEV-" + currentYear + "-001",
                response.number()
        );

        verify(quoteRepository)
                .save(any(Quote.class));
    }

    @Test
    void duplicateShouldCreateNewDraftQuoteWithDuplicatedLines() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .user(user)
                .build();

        Quote originalQuote = Quote.builder()
                .id(1L)
                .number("DEV-2025-001")
                .status(QuoteStatus.ACCEPTED)
                .client(client)
                .user(user)
                .build();

        Quote lastQuote = Quote.builder()
                .id(10L)
                .number("DEV-" + LocalDate.now().getYear() + "-001")
                .build();

        QuoteLine originalLine = QuoteLine.builder()
                .id(1L)
                .quote(originalQuote)
                .description("Développement")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .vatRate(BigDecimal.valueOf(0.20))
                .build();

        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(originalQuote));

        when(quoteRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.of(lastQuote));

        when(quoteRepository.save(any(Quote.class)))
                .thenAnswer(invocation -> {
                    Quote quote = invocation.getArgument(0);

                    // Simule l'id généré par la BDD
                    if (quote.getId() == null) {
                        quote.setId(2L);
                    }

                    return quote;
                });

        when(quoteLineRepository.findByQuote_Id(1L))
                .thenReturn(List.of(originalLine));

        when(quoteLineRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuoteLine duplicatedLine = QuoteLine.builder()
                .id(2L)
                .description("Développement")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(100))
                .vatRate(BigDecimal.valueOf(0.20))
                .build();

        // utilisé dans recalculateTotals() puis dans toQuoteResponse()
        when(quoteLineRepository.findByQuote_Id(2L))
                .thenReturn(List.of(duplicatedLine));

        // Act
        QuoteResponse response =
                quoteService.duplicate(1L, user);

        // Assert

        assertAll(
                () -> assertEquals(
                        QuoteStatus.DRAFT,
                        response.status()
                ),
                () -> assertEquals(
                        client.getId(),
                        response.client().id()
                ),
                () -> assertEquals(
                        1,
                        response.lines().size()
                ),
                () -> assertEquals(
                        "Développement",
                        response.lines().getFirst().description()
                ),
                () -> assertEquals(
                        2,
                        response.lines().getFirst().quantity()
                ),
                () -> assertEquals(
                        0,
                        response.totalHt().compareTo(BigDecimal.valueOf(200))
                ),
                () -> assertEquals(
                        0,
                        response.totalTva().compareTo(BigDecimal.valueOf(40))
                ),
                () -> assertEquals(
                        0,
                        response.totalTtc().compareTo(BigDecimal.valueOf(240))
                ),
                () -> assertEquals(
                        BigDecimal.valueOf(0.20),
                        response.lines().getFirst().vatRate()
                )
        );

        verify(quoteRepository, times(2))
                .save(any(Quote.class));

        verify(quoteLineRepository)
                .saveAll(anyList());
    }

    @Test
    void searchShouldReturnFilteredQuotes() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .number("DEV-2025-001")
                .status(QuoteStatus.DRAFT)
                .client(client)
                .user(user)
                .build();

        QuoteFilterRequest filter =
                new QuoteFilterRequest(
                        "ACME",
                        QuoteStatus.DRAFT,
                        null,
                        null
                );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Quote> page =
                new PageImpl<>(List.of(quote), pageable, 1);

        when(quoteRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<QuoteResponse> responses =
                quoteService.search(filter, pageable, user);

        // Assert
        assertEquals(1, responses.getTotalElements());

        QuoteResponse response = responses.getContent().getFirst();

        assertAll(
                () -> assertEquals("DEV-2025-001", response.number()),
                () -> assertEquals(QuoteStatus.DRAFT, response.status()),
                () -> assertEquals("ACME", response.client().name())
        );

        verify(quoteRepository)
                .findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void markAsSentShouldChangeDraftToPending() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .status(QuoteStatus.DRAFT)
                .client(client)
                .user(user)
                .build();


        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));


        // Act
        quoteService.markAsSent(
                1L,
                user
        );


        // Assert
        assertEquals(
                QuoteStatus.PENDING,
                quote.getStatus()
        );

        verify(quoteRepository)
                .save(quote);
    }

    @Test
    void markAsSentShouldNotChangePendingQuote() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .status(QuoteStatus.PENDING)
                .client(client)
                .user(user)
                .build();


        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));


        // Act
        quoteService.markAsSent(
                1L,
                user
        );


        // Assert
        assertEquals(
                QuoteStatus.PENDING,
                quote.getStatus()
        );

        verify(quoteRepository, never())
                .save(any());
    }

    @Test
    void markAsSentShouldNotChangeAcceptedQuote() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .status(QuoteStatus.ACCEPTED)
                .client(client)
                .user(user)
                .build();


        when(quoteRepository.findById(1L))
                .thenReturn(Optional.of(quote));


        quoteService.markAsSent(
                1L,
                user
        );


        assertEquals(
                QuoteStatus.ACCEPTED,
                quote.getStatus()
        );

        verify(quoteRepository, never())
                .save(any());
    }

}
