package fr.devisgenerator.devisgenerator.dto.request;

import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import jakarta.validation.constraints.NotNull;

public record QuoteRequest(
        @NotNull(message = "Client is required")
        Long clientId,

        @NotNull(message = "Status is required")
        QuoteStatus status) {
}
