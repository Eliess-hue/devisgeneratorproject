package fr.devisgenerator.devisgenerator.dto.response;

import fr.devisgenerator.devisgenerator.enums.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuoteResponse(
        Long id,
        String number,
        QuoteStatus status,
        BigDecimal totalHt,
        BigDecimal totalTva,
        BigDecimal totalTtc,
        LocalDateTime createdAt,
        ClientResponse client,
        AppUserResponse user,
        List<QuoteLineResponse> lines
) {
}
