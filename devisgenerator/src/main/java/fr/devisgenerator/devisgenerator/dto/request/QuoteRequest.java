package fr.devisgenerator.devisgenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuoteRequest(
        @NotNull(message = "Client is required")
        Long clientId,

        @NotBlank(message = "Status is required")
        String status) {
}
