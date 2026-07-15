package fr.devisgenerator.devisgenerator.dto.pdf;

public record GeneratedPdf(
        String filename,
        byte[] content
) {
}