package fr.devisgenerator.devisgenerator.service.impl;


import fr.devisgenerator.devisgenerator.config.CompanyProperties;
import fr.devisgenerator.devisgenerator.dto.email.EmailAttachment;
import fr.devisgenerator.devisgenerator.dto.pdf.GeneratedPdf;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.exception.EmailSendingException;
import fr.devisgenerator.devisgenerator.service.QuotePdfService;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import fr.devisgenerator.devisgenerator.service.email.EmailSender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteEmailServiceImplTest {

    @Mock
    private QuoteService quoteService;

    @Mock
    private QuotePdfService quotePdfService;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private CompanyProperties companyProperties;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private QuoteEmailServiceImpl quoteEmailService;

    @Test
    void sendQuoteShouldSendEmailSuccessfully() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .name("ACME")
                .email("contact@acme.com")
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .number("DEV-2026-001")
                .validUntil(LocalDate.of(2026, 8, 7))
                .client(client)
                .build();

        GeneratedPdf pdf = new GeneratedPdf(
                "DEV-2026-001.pdf",
                new byte[]{1, 2, 3}
        );

        when(quoteService.getAccessibleQuote(1L, user))
                .thenReturn(quote);

        when(quotePdfService.generatePdf(1L, user))
                .thenReturn(pdf);

        when(companyProperties.name())
                .thenReturn("Mon entreprise");

        when(templateEngine.process(
                eq("email/quote"),
                any(Context.class)
        )).thenReturn("<html>Email</html>");

        quoteEmailService.sendQuote(1L, user);

        ArgumentCaptor<EmailAttachment> attachmentCaptor =
                ArgumentCaptor.forClass(
                        EmailAttachment.class
                );

        verify(emailSender)
                .send(
                        eq("contact@acme.com"),
                        eq("Votre devis DEV-2026-001"),
                        eq("<html>Email</html>"),
                        attachmentCaptor.capture()
                );

        EmailAttachment attachment =
                attachmentCaptor.getValue();

        assertEquals(
                "DEV-2026-001.pdf",
                attachment.filename()
        );

        assertArrayEquals(
                new byte[]{1, 2, 3},
                attachment.content()
        );

        verify(quoteService)
                .markAsSent(1L, user);
    }

    @Test
    void sendQuoteShouldThrowExceptionWhenSendingFails() {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .name("ACME")
                .email("contact@acme.com")
                .build();

        Quote quote = Quote.builder()
                .id(1L)
                .number("DEV-2026-001")
                .validUntil(LocalDate.of(2026, 8, 7))
                .client(client)
                .build();

        GeneratedPdf pdf = new GeneratedPdf(
                "DEV-2026-001.pdf",
                new byte[]{1, 2, 3}
        );

        when(quoteService.getAccessibleQuote(1L, user))
                .thenReturn(quote);

        when(quotePdfService.generatePdf(1L, user))
                .thenReturn(pdf);

        when(companyProperties.name())
                .thenReturn("Mon entreprise");

        when(templateEngine.process(
                anyString(),
                any(Context.class)
        )).thenReturn("<html>Email</html>");

        doThrow(
                new EmailSendingException(
                        "Failed to send email",
                        new RuntimeException("SMTP error")
                )
        ).when(emailSender)
                .send(
                        anyString(),
                        anyString(),
                        anyString(),
                        any()
                );

        assertThrows(
                EmailSendingException.class,
                () -> quoteEmailService.sendQuote(
                        1L,
                        user
                )
        );

        verify(
                quoteService,
                never()
        ).markAsSent(
                anyLong(),
                any()
        );
    }
}