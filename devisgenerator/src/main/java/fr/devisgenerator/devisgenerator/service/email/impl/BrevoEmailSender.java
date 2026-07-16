package fr.devisgenerator.devisgenerator.service.email.impl;

import fr.devisgenerator.devisgenerator.config.BrevoProperties;
import fr.devisgenerator.devisgenerator.dto.email.EmailAttachment;
import fr.devisgenerator.devisgenerator.dto.brevo.BrevoAttachment;
import fr.devisgenerator.devisgenerator.dto.brevo.BrevoEmailRequest;
import fr.devisgenerator.devisgenerator.dto.brevo.BrevoRecipient;
import fr.devisgenerator.devisgenerator.dto.brevo.BrevoSender;
import fr.devisgenerator.devisgenerator.exception.EmailSendingException;
import fr.devisgenerator.devisgenerator.service.email.EmailSender;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Base64;
import java.util.List;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class BrevoEmailSender implements EmailSender {

    private final RestClient brevoRestClient;
    private final BrevoProperties brevoProperties;

    @Override
    public void send(
            String to,
            String subject,
            String html,
            EmailAttachment attachment
    ) {

        try {

            String base64Attachment =
                    Base64.getEncoder()
                            .encodeToString(
                                    attachment.content()
                            );

            BrevoEmailRequest request =
                    new BrevoEmailRequest(
                            new BrevoSender(
                                    brevoProperties.senderName(),
                                    brevoProperties.senderEmail()
                            ),
                            List.of(
                                    new BrevoRecipient(to)
                            ),
                            subject,
                            html,
                            List.of(
                                    new BrevoAttachment(
                                            attachment.filename(),
                                            base64Attachment
                                    )
                            )
                    );

            brevoRestClient.post()
                    .uri("/smtp/email")
                    .header(
                            "api-key",
                            brevoProperties.apiKey()
                    )
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException e) {

            throw new EmailSendingException(
                    "Failed to send email via Brevo",
                    e
            );
        }
    }
}