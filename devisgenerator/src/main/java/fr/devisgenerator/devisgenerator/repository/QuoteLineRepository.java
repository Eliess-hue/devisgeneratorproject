package fr.devisgenerator.devisgenerator.repository;

import fr.devisgenerator.devisgenerator.entity.QuoteLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuoteLineRepository extends JpaRepository<QuoteLine, Long> {

    List<QuoteLine> findByQuote_Id(Long quoteId);

}
