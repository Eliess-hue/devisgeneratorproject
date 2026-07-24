package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.QuoteFilterRequest;
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
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.exception.ClientNotFoundException;
import fr.devisgenerator.devisgenerator.exception.InvalidQuoteLineException;
import fr.devisgenerator.devisgenerator.exception.QuoteLineNotFoundException;
import fr.devisgenerator.devisgenerator.exception.QuoteNotFoundException;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import fr.devisgenerator.devisgenerator.specification.QuoteSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final ClientRepository clientRepository;
    private final QuoteLineRepository quoteLineRepository;

    @Transactional
    @Override
    public QuoteResponse create(QuoteRequest request, AppUser user) {

        Client client = getAccessibleClient(request.clientId(), user);

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


        log.info(
                "Quote {} created by user {} for client {}",
                quote.getNumber(),
                user.getId(),
                client.getId()
        );

        return toQuoteResponse(quote);

    }

    @Override
    public QuoteResponse findById(Long id, AppUser user) {

        return toQuoteResponse(
                getAccessibleQuote(id, user)
        );
    }

    @Override
    public QuoteResponse update(Long id, QuoteRequest request, AppUser user) {

        Quote quote = getAccessibleQuote(id, user);

        quote.setStatus(request.status());

        quote = quoteRepository.save(quote);

        log.info(
                "Quote {} updated by user {}",
                quote.getNumber(),
                user.getId()
        );

        return toQuoteResponse(quote);
    }

    @Override
    public void delete(Long id, AppUser user) {

        Quote quote = getAccessibleQuote(id, user);

        log.info(
                "Quote {} deleted by user {}",
                quote.getNumber(),
                user.getId()
        );

        quoteRepository.delete(quote);
    }

    @Transactional
    @Override
    public QuoteResponse duplicate(Long id, AppUser user) {

        Quote originalQuote = getAccessibleQuote(id, user);

        Quote newQuote = Quote.builder()
                .number(generateQuoteNumber())
                .status(QuoteStatus.DRAFT)
                .client(originalQuote.getClient())
                .user(user)
                .totalHt(BigDecimal.ZERO)
                .totalTva(BigDecimal.ZERO)
                .totalTtc(BigDecimal.ZERO)
                .build();

        Quote duplicatedQuote = quoteRepository.save(newQuote);

        List<QuoteLine> originalLines =
                quoteLineRepository.findByQuote_Id(originalQuote.getId());

        List<QuoteLine> duplicatedLines =
                originalLines.stream()
                        .map(line ->
                                QuoteLine.builder()
                                        .quote(duplicatedQuote)
                                        .description(line.getDescription())
                                        .quantity(line.getQuantity())
                                        .unitPrice(line.getUnitPrice())
                                        .vatRate(line.getVatRate())
                                        .build()
                        )
                        .toList();

        quoteLineRepository.saveAll(duplicatedLines);

        recalculateTotals(duplicatedQuote);

        log.info(
                "Quote {} duplicated into {} by user {}",
                originalQuote.getNumber(),
                duplicatedQuote.getNumber(),
                user.getId()
        );

        return toQuoteResponse(duplicatedQuote);
    }

    @Override
    public Page<QuoteResponse> search(QuoteFilterRequest filter, Pageable pageable, AppUser user) {

        Specification<Quote> spec = Specification.allOf(
                QuoteSpecification.hasSearch(filter.search()),
                QuoteSpecification.hasStatus(filter.status()),
                QuoteSpecification.isBetweenDates(
                        filter.from(),
                        filter.to()
                )
        );

        if (!isAdmin(user)) {
            spec = spec.and(QuoteSpecification.hasUser(user.getId()));
        }

        return quoteRepository
                .findAll(spec, pageable)
                .map(this::toQuoteResponse);
    }

    private boolean isAdmin(AppUser user) {
        return UserRole.ROLE_ADMIN.name().equals(user.getRole());
    }

    @Override
    public Quote getAccessibleQuote(Long id, AppUser user) {

        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() ->
                        new QuoteNotFoundException(
                                "Quote " + id + " not found"
                        ));

        if (isAdmin(user)) {
            return quote;
        }

        if (!quote.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Access denied: user "
                            + user.getId()
                            + " attempted to access quote "
                            + id
            );
        }

        return quote;

    }

    // Lines

    @Transactional
    @Override
    public QuoteResponse addLine(Long quoteId, QuoteLineRequest request, AppUser user) {

        Quote quote = getAccessibleQuote(quoteId, user);

        QuoteLine line = QuoteLine.builder()
                .quote(quote)
                .description(request.description())
                .quantity(request.quantity())
                .unitPrice(request.unitPrice())
                .vatRate(request.vatRate())
                .build();

        quoteLineRepository.save(line);

        recalculateTotals(quote);

        log.info(
                "Line {} added to quote {} by user {}",
                line.getId(),
                quote.getNumber(),
                user.getId()
        );

        return toQuoteResponse(quote);
    }

    @Transactional
    @Override
    public QuoteResponse updateLine(Long quoteId, Long lineId, QuoteLineRequest request, AppUser user) {

        Quote quote = getAccessibleQuote(quoteId, user);

        QuoteLine line = getValidatedQuoteLine(
                quote.getId(),
                lineId
        );

        line.setDescription(request.description());
        line.setQuantity(request.quantity());
        line.setUnitPrice(request.unitPrice());
        line.setVatRate(request.vatRate());

        quoteLineRepository.save(line);

        recalculateTotals(quote);

        log.info(
                "Line {} updated in quote {} by user {}",
                line.getId(),
                quote.getNumber(),
                user.getId()
        );

        return toQuoteResponse(quote);

    }

    @Transactional
    @Override
    public QuoteResponse deleteLine(Long quoteId, Long lineId, AppUser user) {

        Quote quote = getAccessibleQuote(quoteId, user);

        QuoteLine line = getValidatedQuoteLine(
                quote.getId(),
                lineId
        );

        log.info(
                "Line {} removed from quote {} by user {}",
                line.getId(),
                quote.getNumber(),
                user.getId()
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
                        line.getVatRate(),
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



    @Override
    public void markAsSent(Long id, AppUser user) {

        Quote quote = getAccessibleQuote(id, user);

        if (quote.getStatus() != QuoteStatus.DRAFT) {
            return;
        }

        quote.setStatus(QuoteStatus.PENDING);
        quoteRepository.save(quote);

        log.info(
                "Quote {} marked as PENDING by user {}",
                quote.getNumber(),
                user.getId()
        );

    }


    private Client getAccessibleClient(Long id, AppUser user) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Client " + id + " not found"
                        ));

        if (isAdmin(user)) {
            return client;
        }

        if (!client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Access denied: user "
                            + user.getId()
                            + " attempted to access client "
                            + id
            );
        }

        return client;
    }

    private QuoteLine getValidatedQuoteLine(
            Long quoteId,
            Long lineId) {

        QuoteLine line = quoteLineRepository.findById(lineId)
                .orElseThrow(() ->
                        new QuoteLineNotFoundException(
                                "Quote line " + lineId + " not found"
                        ));

        if (!line.getQuote().getId().equals(quoteId)) {
            throw new InvalidQuoteLineException(
                    "Quote line "
                            + lineId
                            + " does not belong to quote "
                            + quoteId
            );
        }

        return line;
    }

    private void recalculateTotals(Quote quote) {

        List<QuoteLine> lines =
                quoteLineRepository.findByQuote_Id(
                        quote.getId()
                );

        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;

        for (QuoteLine line : lines) {

            BigDecimal lineTotalHt =
                    line.getUnitPrice()
                            .multiply(BigDecimal.valueOf(line.getQuantity()));

            BigDecimal lineTva =
                    lineTotalHt.multiply(
                            line.getVatRate()
                    );

            totalHt = totalHt.add(lineTotalHt);
            totalTva = totalTva.add(lineTva);
        }

        BigDecimal totalTtc = totalHt.add(totalTva);

        quote.setTotalHt(totalHt);
        quote.setTotalTva(totalTva);
        quote.setTotalTtc(totalTtc);

        quoteRepository.save(quote);
    }

}
