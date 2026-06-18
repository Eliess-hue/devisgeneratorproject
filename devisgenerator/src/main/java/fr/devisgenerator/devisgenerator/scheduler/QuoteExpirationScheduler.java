package fr.devisgenerator.devisgenerator.scheduler;

import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteExpirationScheduler {

    private final QuoteRepository quoteRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void expireQuote() {

        List<Quote> expiredQuotes =
                quoteRepository
                        .findByValidUntilBeforeAndStatusNot(
                                LocalDate.now(),
                                QuoteStatus.EXPIRED
                        );

        expiredQuotes.forEach(
                quote -> quote.setStatus(
                        QuoteStatus.EXPIRED
                )
        );

        quoteRepository.saveAll(expiredQuotes);

        log.info(
                "{} devis expirés",
                expiredQuotes.size()
        );

    }

}
