package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.config.CompanyProperties;
import fr.devisgenerator.devisgenerator.dto.pdf.GeneratedPdf;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.exception.EmailSendingException;
import fr.devisgenerator.devisgenerator.service.QuotePdfService;
import fr.devisgenerator.devisgenerator.service.QuoteService;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class QuoteEmailServiceImplTest {

    @Mock
    private QuoteService quoteService;

    @Mock
    private QuotePdfService quotePdfService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private CompanyProperties companyProperties;


    @InjectMocks
    private QuoteEmailServiceImpl quoteEmailService;

    @Test
    void sendQuoteShouldSendEmailSuccessfully() throws Exception {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();


        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .build();


        Quote quote = Quote.builder()
                .id(1L)
                .number("DEV-2026-001")
                .client(client)
                .validUntil(
                        LocalDate.of(2026, 8, 7)
                )
                .build();


        GeneratedPdf pdf =
                new GeneratedPdf(
                        "DEV-2026-001.pdf",
                        new byte[]{1,2,3}
                );


        when(quoteService.getOwnedQuote(1L,user))
                .thenReturn(quote);


        when(quotePdfService.generatePdf(1L,user))
                .thenReturn(pdf);


        when(templateEngine.process(
                anyString(),
                any(Context.class)
        ))
                .thenReturn(
                        "<html>Email</html>"
                );


        when(companyProperties.name())
                .thenReturn("Mon entreprise");


        Session session =
                Session.getInstance(
                        new Properties()
                );

        MimeMessage mimeMessage =
                new MimeMessage(session);


        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);



        quoteEmailService.sendQuote(
                1L,
                user
        );



        ArgumentCaptor<MimeMessage> captor =
                ArgumentCaptor.forClass(
                        MimeMessage.class
                );


        verify(mailSender)
                .send(
                        captor.capture()
                );


        MimeMessage sent =
                captor.getValue();


        assertEquals(
                "Votre devis DEV-2026-001",
                sent.getSubject()
        );


        verify(quoteService)
                .markAsSent(
                        1L,
                        user
                );
    }

    @Test
    void sendQuoteShouldThrowExceptionWhenEmailFails() throws Exception {

        AppUser user = AppUser.builder()
                .id(1L)
                .build();


        Client client = Client.builder()
                .email("contact@acme.com")
                .name("ACME")
                .build();


        Quote quote = Quote.builder()
                .id(1L)
                .number("DEV-2026-001")
                .client(client)
                .validUntil(
                        LocalDate.of(2026, 8, 7)
                )
                .build();


        GeneratedPdf pdf =
                new GeneratedPdf(
                        "DEV-2026-001.pdf",
                        new byte[]{1,2,3}
                );


        when(quoteService.getOwnedQuote(1L,user))
                .thenReturn(quote);


        when(quotePdfService.generatePdf(1L,user))
                .thenReturn(pdf);


        when(templateEngine.process(
                anyString(),
                any(Context.class)
        ))
                .thenReturn("<html>Email</html>");


        MimeMessage mimeMessage =
                new MimeMessage(
                        Session.getInstance(new Properties())
                );


        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);


        doThrow(
                new MailException("SMTP error") {}
        )
                .when(mailSender)
                .send(any(MimeMessage.class));


        assertThrows(
                EmailSendingException.class,
                () ->
                        quoteEmailService.sendQuote(
                                1L,
                                user
                        )
        );


        verify(quoteService, never())
                .markAsSent(
                        anyLong(),
                        any()
                );
    }

}
