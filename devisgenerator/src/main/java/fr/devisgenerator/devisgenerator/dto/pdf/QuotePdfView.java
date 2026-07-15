package fr.devisgenerator.devisgenerator.dto.pdf;

import java.util.List;

public record QuotePdfView(
        String number,
        String status,
        String createdAt,
        String validUntil,
        ClientPdfView client,
        IssuerPdfView issuer,
        List<QuoteLinePdfView> lines,
        String totalHt,
        String totalTva,
        String totalTtc
) {
}
