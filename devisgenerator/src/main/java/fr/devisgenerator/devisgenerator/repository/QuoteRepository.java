package fr.devisgenerator.devisgenerator.repository;

import fr.devisgenerator.devisgenerator.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByUser_Id(Long userId);
    Optional<Quote> findTopByOrderByIdDesc();

    int countByClient_Id(Long clientId);

    Optional<Quote> findTopByClient_IdOrderByCreatedAtDesc(
            Long clientId
    );

}
