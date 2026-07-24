package fr.devisgenerator.devisgenerator.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import fr.devisgenerator.devisgenerator.config.CompanyProperties;
import fr.devisgenerator.devisgenerator.dto.pdf.*;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.entity.QuoteLine;
import fr.devisgenerator.devisgenerator.exception.PdfGenerationException;
import fr.devisgenerator.devisgenerator.repository.QuoteLineRepository;
import fr.devisgenerator.devisgenerator.service.QuotePdfService;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import fr.devisgenerator.devisgenerator.util.PdfFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotePdfServiceImpl implements QuotePdfService {

    private final QuoteService quoteService;
    private final QuoteLineRepository quoteLineRepository;
    private final CompanyProperties companyProperties;
    private final SpringTemplateEngine templateEngine;

    @Override
    public GeneratedPdf generatePdf(Long quoteId, AppUser user) {

        Quote quote = quoteService.getAccessibleQuote(quoteId, user);

        QuotePdfView pdfView = buildPdfView(quote);

        String html = generateHtml(pdfView);

        byte[] pdf = generatePdfBytes(html);

        log.info(
                "PDF generated for quote {} by user {}",
                quote.getNumber(),
                user.getId()
        );

        return new GeneratedPdf(
                quote.getNumber() + ".pdf",
                pdf
        );

    }

    private QuotePdfView buildPdfView(Quote quote) {

        List<QuoteLinePdfView> lines =
                quoteLineRepository
                        .findByQuote_Id(
                                quote.getId()
                        )
                        .stream()
                        .map(this::buildLinePdfView)
                        .toList();

        ClientPdfView client =
                buildClientPdfView(
                        quote.getClient()
                );

        IssuerPdfView issuer =
                buildIssuerPdfView();

        return new QuotePdfView(
                quote.getNumber(),
                PdfFormatter.formatStatus(
                        quote.getStatus()
                ),
                PdfFormatter.formatDateTime(
                        quote.getCreatedAt()
                ),
                PdfFormatter.formatDate(
                        quote.getValidUntil()
                ),
                client,
                issuer,
                lines,
                PdfFormatter.formatMoney(
                        quote.getTotalHt()
                ),
                PdfFormatter.formatMoney(
                        quote.getTotalTva()
                ),
                PdfFormatter.formatMoney(
                        quote.getTotalTtc()
                )
        );
    }

    private QuoteLinePdfView buildLinePdfView(QuoteLine line) {

        BigDecimal totalHt = line.getUnitPrice()
                .multiply(
                        BigDecimal.valueOf(
                                line.getQuantity()
                        )
                );

        BigDecimal totalTva = totalHt.multiply(
                line.getVatRate()
        );

        BigDecimal totalTtc = totalHt.add(
                totalTva
        );

        return new QuoteLinePdfView(
                line.getDescription(),
                line.getQuantity(),
                PdfFormatter.formatMoney(
                        line.getUnitPrice()
                ),
                PdfFormatter.formatVatRate(
                        line.getVatRate()
                ),
                PdfFormatter.formatMoney(
                        totalHt
                ),
                PdfFormatter.formatMoney(
                        totalTva
                ),
                PdfFormatter.formatMoney(
                        totalTtc
                )
        );
    }

    private ClientPdfView buildClientPdfView(Client client) {
        return new ClientPdfView(
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress()
        );
    }

    private IssuerPdfView buildIssuerPdfView() {

        return new IssuerPdfView(
                companyProperties.name(),
                companyProperties.address(),
                companyProperties.email(),
                companyProperties.phone()
        );
    }

    private String generateHtml(QuotePdfView view) {
        Context context = new Context();

        context.setVariable(
                "quote",
                view
        );

        return templateEngine.process(
                "pdf/quote",
                context
        );
    }

    private byte[] generatePdfBytes(String html) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException("Erreur lors de la génération du PDF", e);
        }
    }

}
