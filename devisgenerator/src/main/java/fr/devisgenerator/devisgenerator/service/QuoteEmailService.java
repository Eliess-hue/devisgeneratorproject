package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.entity.AppUser;

public interface QuoteEmailService {

    void sendQuote(Long quoteId, AppUser user);

}
