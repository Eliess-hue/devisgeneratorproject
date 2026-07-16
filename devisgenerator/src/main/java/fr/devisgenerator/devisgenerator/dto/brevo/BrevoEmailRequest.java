package fr.devisgenerator.devisgenerator.dto.brevo;

import java.util.List;

public record BrevoEmailRequest(
        BrevoSender sender,
        List<BrevoRecipient> to,
        String subject,
        String htmlContent,
        List<BrevoAttachment> attachment
) {
}