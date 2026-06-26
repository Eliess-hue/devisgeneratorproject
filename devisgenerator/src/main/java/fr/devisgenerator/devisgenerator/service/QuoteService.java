package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.request.QuoteLineRequest;
import fr.devisgenerator.devisgenerator.dto.request.QuoteRequest;
import fr.devisgenerator.devisgenerator.dto.response.QuoteResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;

import java.util.List;

public interface QuoteService {

    QuoteResponse create(QuoteRequest request, AppUser user);

    List<QuoteResponse> findAll(AppUser user);

    QuoteResponse findById(Long id, AppUser user);

    QuoteResponse update(Long id, QuoteRequest request, AppUser user);

    void delete(Long id, AppUser user);

    QuoteResponse duplicate(Long id, AppUser user);

    // Gestion des lignes
    QuoteResponse addLine(Long quoteId, QuoteLineRequest request, AppUser user);

    QuoteResponse updateLine(Long quoteId, Long lineId, QuoteLineRequest request, AppUser user);

    QuoteResponse deleteLine(Long quoteId, Long lineId, AppUser user);

}