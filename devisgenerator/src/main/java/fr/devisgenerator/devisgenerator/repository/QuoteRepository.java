package fr.devisgenerator.devisgenerator.repository;

import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByUser_Id(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Quote> findTopByOrderByIdDesc();

    int countByClient_Id(Long clientId);

    Optional<Quote> findTopByClient_IdOrderByCreatedAtDesc(
            Long clientId
    );

    List<Quote> findByValidUntilBeforeAndStatusNot(
            LocalDate date,
            QuoteStatus status
    );

}
