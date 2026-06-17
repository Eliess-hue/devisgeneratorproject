package fr.devisgenerator.devisgenerator.dto.response;

import fr.devisgenerator.devisgenerator.enums.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DashboardQuoteResponse(
        Long id,
        String quoteNumber,
        String clientName,
        QuoteStatus status,
        LocalDateTime createdAt,
        BigDecimal totalTtc
) {
}