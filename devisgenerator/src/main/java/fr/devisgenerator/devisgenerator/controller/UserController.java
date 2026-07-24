package fr.devisgenerator.devisgenerator.controller;

import fr.devisgenerator.devisgenerator.dto.request.ChangeUserRoleRequest;
import fr.devisgenerator.devisgenerator.dto.response.AppUserResponse;
import fr.devisgenerator.devisgenerator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
        name = "Roles utilisateurs",
        description = "Gestion des roles utilisateurs"
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Lister les utilisateurs",
            description = "Permet de lister et visualiser les utilisateurs"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AppUserResponse> findAll() {

        return userService.findAll();
    }

    @Operation(
            summary = "Mettre à jour les roles",
            description = "Met à jour les roles utilisateurs"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<AppUserResponse> changeRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserRoleRequest request
    ) {

        AppUserResponse response =
                userService.changeRole(
                        id,
                        request.role()
                );

        return ResponseEntity.ok(response);
    }

}