package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.config.CompanyProperties;
import fr.devisgenerator.devisgenerator.dto.pdf.GeneratedPdf;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.exception.EmailSendingException;
import fr.devisgenerator.devisgenerator.service.QuoteEmailService;
import fr.devisgenerator.devisgenerator.service.QuotePdfService;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import fr.devisgenerator.devisgenerator.util.PdfFormatter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class QuoteEmailServiceImpl implements QuoteEmailService {

    private final QuoteService quoteService;
    private final QuotePdfService quotePdfService;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final CompanyProperties companyProperties;

    @Override
    @Transactional
    public void sendQuote(Long quoteId, AppUser user) {

        Quote quote = quoteService.getOwnedQuote(quoteId, user);

        GeneratedPdf pdf = quotePdfService.generatePdf(quoteId, user);

        try {
            Context context = buildContext(quote);

            String html = templateEngine.process("email/quote", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, StandardCharsets.UTF_8.name()
            );

            helper.setTo(quote.getClient().getEmail());
            helper.setSubject("Votre devis " + quote.getNumber());
            helper.setText(html, true);
            helper.addAttachment(
                    pdf.filename(),
                    new ByteArrayResource(pdf.content())
            );

            mailSender.send(message);

            quoteService.markAsSent(quoteId, user);

        } catch (MessagingException | MailException e) {
            throw new EmailSendingException("Unable to send quote email", e);
        }
    }

    private Context buildContext(Quote quote) {
        Context context = new Context();
        context.setVariable("clientName", quote.getClient().getName());
        context.setVariable("quoteNumber", quote.getNumber());
        context.setVariable("validUntil", PdfFormatter.formatDate(quote.getValidUntil()));
        context.setVariable("companyName", companyProperties.name());
        return context;
    }
}
