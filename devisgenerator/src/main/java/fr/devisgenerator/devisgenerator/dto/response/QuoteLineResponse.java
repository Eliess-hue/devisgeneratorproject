package fr.devisgenerator.devisgenerator.dto.response;

import java.math.BigDecimal;

public record QuoteLineResponse(
        Long id,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {
}
