package fr.devisgenerator.devisgenerator.dto.email;

public record EmailAttachment(
        String filename,
        byte[] content
) {
}
