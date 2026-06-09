package fr.devisgenerator.devisgenerator.dto.response;

public record ClientResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        Integer quoteCount,
        String lastQuoteNumber
) {
}
