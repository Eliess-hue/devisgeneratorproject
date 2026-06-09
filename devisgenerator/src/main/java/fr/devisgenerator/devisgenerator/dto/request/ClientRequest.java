package fr.devisgenerator.devisgenerator.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        String phone,
        String address
) {
}
