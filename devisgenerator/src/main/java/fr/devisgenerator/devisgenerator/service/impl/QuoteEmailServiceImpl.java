package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.config.CompanyProperties;
import fr.devisgenerator.devisgenerator.dto.email.EmailAttachment;
import fr.devisgenerator.devisgenerator.dto.pdf.GeneratedPdf;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.service.QuoteEmailService;
import fr.devisgenerator.devisgenerator.service.QuotePdfService;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import fr.devisgenerator.devisgenerator.service.email.EmailSender;
import fr.devisgenerator.devisgenerator.util.PdfFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuoteEmailServiceImpl implements QuoteEmailService {

    private final QuoteService quoteService;
    private final QuotePdfService quotePdfService;
    private final SpringTemplateEngine templateEngine;
    private final CompanyProperties companyProperties;
    private final EmailSender emailSender;

    @Override
    public void sendQuote(Long quoteId, AppUser user) {

        Quote quote =
                quoteService.getOwnedQuote(
                        quoteId,
                        user
                );

        GeneratedPdf pdf =
                quotePdfService.generatePdf(
                        quoteId,
                        user
                );

        Context context =
                buildContext(quote);

        String html =
                templateEngine.process(
                        "email/quote",
                        context
                );

        EmailAttachment attachment =
                new EmailAttachment(
                        pdf.filename(),
                        pdf.content()
                );

        emailSender.send(
                quote.getClient().getEmail(),
                "Votre devis " + quote.getNumber(),
                html,
                attachment
        );

        quoteService.markAsSent(
                quoteId,
                user
        );

        log.info(
                "Quote {} sent by email to client {} by user {}",
                quote.getNumber(),
                quote.getClient().getId(),
                user.getId()
        );
    }

    private Context buildContext(Quote quote) {

        Context context = new Context();

        context.setVariable(
                "clientName",
                quote.getClient().getName()
        );

        context.setVariable(
                "quoteNumber",
                quote.getNumber()
        );

        context.setVariable(
                "validUntil",
                PdfFormatter.formatDate(
                        quote.getValidUntil()
                )
        );

        context.setVariable(
                "companyName",
                companyProperties.name()
        );

        return context;
    }
}
