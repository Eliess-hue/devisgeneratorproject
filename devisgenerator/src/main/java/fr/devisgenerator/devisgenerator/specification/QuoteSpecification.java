package fr.devisgenerator.devisgenerator.specification;

import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class QuoteSpecification {

    public static Specification<Quote> hasSearch(String search) {

        if (search == null || search.isBlank()) {
            return null;
        }

        return (root, query, cb) -> cb.or(
                cb.like(
                        cb.lower(root.get("number")),
                        "%" + search.toLowerCase() + "%"
                ),
                cb.like(
                        cb.lower(root.get("client").get("name")),
                        "%" + search.toLowerCase() + "%"
                )
        );

    }

    public static Specification<Quote> hasStatus(QuoteStatus status) {

        if (status == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("status"),
                        status
                );

    }

    public static Specification<Quote> isBetweenDates(LocalDate from, LocalDate to) {

        if (from == null && to == null) {
            return null;
        }

        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(
                        root.get("createdAt"),
                        from.atStartOfDay(),
                        to.atTime(23, 59, 59)
                );
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        from.atStartOfDay()
                );
            }

            return cb.lessThanOrEqualTo(
                    root.get("createdAt"),
                    to.atTime(23, 59, 59)
            );
        };

    }

    public static Specification<Quote> hasUser(Long userId) {

        if (userId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

}
