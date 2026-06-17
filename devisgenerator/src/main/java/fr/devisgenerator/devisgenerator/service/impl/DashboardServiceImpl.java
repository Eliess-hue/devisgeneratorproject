package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.response.DashboardQuoteResponse;
import fr.devisgenerator.devisgenerator.dto.response.MonthlyRevenueResponse;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import fr.devisgenerator.devisgenerator.dto.response.DashboardResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{

    private final QuoteRepository quoteRepository;


    @Override
    public DashboardResponse getDashboard(AppUser user) {

        List<Quote> quotes =
                quoteRepository.findByUser_Id(
                        user.getId()
                );

        long totalQuotes =
                quotes.size();

        long pendingQuotes =
                quotes.stream()
                        .filter(quote ->
                                quote.getStatus() == QuoteStatus.PENDING
                                )
                        .count();

        long acceptedQuotes =
                quotes.stream()
                        .filter(quote ->
                                quote.getStatus() == QuoteStatus.ACCEPTED
                        )
                        .count();

        BigDecimal totalRevenue =
                quotes.stream()
                        .map(Quote::getTotalTtc)
                        .filter(Objects::nonNull)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        List<DashboardQuoteResponse> recentQuotes =
                quotes.stream()
                        .sorted(
                                Comparator.comparing(
                                        Quote::getCreatedAt
                                ).reversed()
                        )
                        .limit(5)
                        .map(quote ->
                                new DashboardQuoteResponse(
                                        quote.getNumber(),
                                        quote.getClient().getName(),
                                        quote.getStatus(),
                                        quote.getCreatedAt(),
                                        quote.getTotalTtc()
                                )
                        )
                        .toList();

        Map<YearMonth, BigDecimal> revenuesByMonth =
                quotes.stream()
                        .filter(q -> q.getTotalTtc() != null)
                        .collect(
                                Collectors.groupingBy(
                                        quote ->
                                                YearMonth.from(
                                                        quote.getCreatedAt()
                                                ),
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                Quote::getTotalTtc,
                                                BigDecimal::add
                                        )
                                )
                        );

        List<MonthlyRevenueResponse> monthlyRevenues =
                revenuesByMonth.entrySet()
                        .stream()
                        .sorted(
                                Map.Entry.comparingByKey()
                        )
                        .map(entry ->
                                new MonthlyRevenueResponse(
                                        entry.getKey().toString(),
                                        entry.getValue()
                                )
                        )
                        .toList();

        return new DashboardResponse(
                totalQuotes,
                pendingQuotes,
                acceptedQuotes,
                totalRevenue,
                monthlyRevenues,
                recentQuotes
        );

    }
}
