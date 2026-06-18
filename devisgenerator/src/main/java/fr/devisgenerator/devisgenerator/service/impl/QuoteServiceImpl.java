package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.QuoteLineRequest;
import fr.devisgenerator.devisgenerator.dto.request.QuoteRequest;
import fr.devisgenerator.devisgenerator.dto.response.AppUserResponse;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.dto.response.QuoteLineResponse;
import fr.devisgenerator.devisgenerator.dto.response.QuoteResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.entity.QuoteLine;
import fr.devisgenerator.devisgenerator.exception.ClientNotFoundException;
import fr.devisgenerator.devisgenerator.exception.InvalidQuoteLineException;
import fr.devisgenerator.devisgenerator.exception.QuoteLineNotFoundException;
import fr.devisgenerator.devisgenerator.exception.QuoteNotFoundException;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final ClientRepository clientRepository;
    private final QuoteLineRepository quoteLineRepository;

    @Transactional
    @Override
    public QuoteResponse create(QuoteRequest request, AppUser user) {

        Client client = getOwnedClient(request.clientId(), user);

        String quoteNumber = generateQuoteNumber();

        Quote quote = Quote.builder()
                .number(quoteNumber)
                .status(request.status())
                .totalHt(BigDecimal.ZERO)
                .totalTva(BigDecimal.ZERO)
                .totalTtc(BigDecimal.ZERO)
                .client(client)
                .user(user)
                .build();

        quote = quoteRepository.save(quote);

        return toQuoteResponse(quote);

    }

    @Override
    public List<QuoteResponse> findAll(AppUser user) {

        return quoteRepository.findByUser_Id(user.getId())
                .stream()
                .map(this::toQuoteResponse)
                .toList();
    }

    @Override
    public QuoteResponse findById(Long id, AppUser user) {

        return toQuoteResponse(
                getOwnedQuote(id, user)
        );
    }

    @Override
    public QuoteResponse update(Long id, QuoteRequest request, AppUser user) {

        Quote quote = getOwnedQuote(id, user);

        Client client = getOwnedClient(
                request.clientId(),
                user
        );

        quote.setStatus(request.status());
        quote.setClient(client);

        quote = quoteRepository.save(quote);

        return toQuoteResponse(quote);
    }

    @Override
    public void delete(Long id, AppUser user) {

        quoteRepository.delete(
                getOwnedQuote(id, user)
        );
    }

    @Transactional
    @Override
    public QuoteResponse addLine(Long quoteId, QuoteLineRequest request, AppUser user) {

        Quote quote = getOwnedQuote(quoteId, user);

        QuoteLine line = QuoteLine.builder()
                .quote(quote)
                .description(request.description())
                .quantity(request.quantity())
                .unitPrice(request.unitPrice())
                .build();

        quoteLineRepository.save(line);

        recalculateTotals(quote);

        return toQuoteResponse(quote);
    }

    @Transactional
    @Override
    public QuoteResponse deleteLine(Long quoteId, Long lineId, AppUser user) {

        Quote quote = getOwnedQuote(quoteId, user);

        QuoteLine line = getOwnedQuoteLine(
                quote.getId(),
                lineId
        );

        quoteLineRepository.delete(line);

        recalculateTotals(quote);

        return toQuoteResponse(quote);
    }

    private ClientResponse toClientResponse(Client client) {

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                0,
                "-"
        );
    }

    private AppUserResponse toAppUserResponse(AppUser user) {

        return new AppUserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

    private QuoteResponse toQuoteResponse(Quote quote) {

        List<QuoteLineResponse> lines = quoteLineRepository
                .findByQuote_Id(quote.getId())
                .stream()
                .map(line -> new QuoteLineResponse(
                        line.getId(),
                        line.getDescription(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getUnitPrice().multiply(
                                BigDecimal.valueOf(line.getQuantity())
                        )
                ))
                .toList();

        return new QuoteResponse(
                quote.getId(),
                quote.getNumber(),
                quote.getStatus(),
                quote.getTotalHt(),
                quote.getTotalTva(),
                quote.getTotalTtc(),
                quote.getCreatedAt(),
                quote.getValidUntil(),
                toClientResponse(quote.getClient()),
                toAppUserResponse(quote.getUser()),
                lines
        );
    }

    private String generateQuoteNumber() {

        Optional<Quote> lastQuote =
                quoteRepository.findTopByOrderByIdDesc();

        int year = LocalDate.now().getYear();

        if (lastQuote.isEmpty()) {
            return "DEV-" + year + "-001";
        }

        String lastNumber =
                lastQuote.get().getNumber();

        String[] parts =
                lastNumber.split("-");

        String lastYear = parts[1];

        if (!lastYear.equals(String.valueOf(year))) {
            return "DEV-" + year + "-001";
        }

        int counter =
                Integer.parseInt(parts[2]);

        counter++;

        String nextCounter =
                String.format("%03d", counter);

        return "DEV-" + year + "-" + nextCounter;
    }

    private Quote getOwnedQuote(Long id, AppUser user) {

        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException("Quote not found"));

        if (!quote.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return quote;
    }

    private Client getOwnedClient(Long id, AppUser user) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        if (!client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return client;
    }

    private QuoteLine getOwnedQuoteLine(
            Long quoteId,
            Long lineId) {

        QuoteLine line = quoteLineRepository.findById(lineId)
                .orElseThrow(() ->
                        new QuoteLineNotFoundException("Quote line not found"));

        if (!line.getQuote().getId().equals(quoteId)) {
            throw new InvalidQuoteLineException(
                    "Quote line does not belong to quote"
            );
        }

        return line;
    }

    private void recalculateTotals(Quote quote) {

        List<QuoteLine> lines =
                quoteLineRepository.findByQuote_Id(
                        quote.getId()
                );

        BigDecimal totalHt = lines.stream()
                .map(line ->
                        line.getUnitPrice().multiply(
                                BigDecimal.valueOf(
                                        line.getQuantity()
                                )
                        )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal totalTva =
                totalHt.multiply(
                        BigDecimal.valueOf(0.20)
                );

        BigDecimal totalTtc =
                totalHt.add(totalTva);

        quote.setTotalHt(totalHt);
        quote.setTotalTva(totalTva);
        quote.setTotalTtc(totalTtc);

        quoteRepository.save(quote);
    }

}
