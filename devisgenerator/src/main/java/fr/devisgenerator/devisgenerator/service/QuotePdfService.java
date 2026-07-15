package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.dto.pdf.GeneratedPdf;

public interface QuotePdfService {

    GeneratedPdf generatePdf(
            Long quoteId,
            AppUser user
    );

}
