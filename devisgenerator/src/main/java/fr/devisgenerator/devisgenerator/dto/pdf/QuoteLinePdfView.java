package fr.devisgenerator.devisgenerator.dto.pdf;

public record QuoteLinePdfView(
        String description,
        Integer quantity,
        String unitPrice,
        String vatRate,
        String totalHt,
        String totalTva,
        String totalTtc
) {
}
