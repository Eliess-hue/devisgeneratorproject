package fr.devisgenerator.devisgenerator.dto.response;

import fr.devisgenerator.devisgenerator.enums.UserRole;

public record AppUserResponse(
        Long id,
        String username,
        UserRole role
) {
}
