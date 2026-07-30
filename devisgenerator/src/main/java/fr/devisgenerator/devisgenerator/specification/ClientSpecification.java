package fr.devisgenerator.devisgenerator.specification;

import fr.devisgenerator.devisgenerator.entity.Client;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecification {

    public static Specification<Client> hasSearch(String search) {

        return (root, query, cb) -> {

            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + search.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Client> hasUser(Long userId) {

        return (root, query, cb) ->
                cb.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

}