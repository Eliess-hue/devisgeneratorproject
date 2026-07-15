package fr.devisgenerator.devisgenerator.dto.pdf;

public record ClientPdfView(
        String name,
        String email,
        String phone,
        String address
) {
}
