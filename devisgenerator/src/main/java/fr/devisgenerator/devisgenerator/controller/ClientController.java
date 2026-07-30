package fr.devisgenerator.devisgenerator.controller;

import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.request.ClientFilterRequest;
import fr.devisgenerator.devisgenerator.dto.response.PageResponse;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Clients",
        description = "Gestion des clients"
)
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(
            summary = "Créer un client",
            description = "Crée un nouveau client pour l'utilisateur connecté"
    )
    @PostMapping
    public ResponseEntity<ClientResponse> create(
            @Valid @RequestBody ClientRequest request,
            @AuthenticationPrincipal AppUser user) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(request, user));
    }

    @Operation(
            summary = "Filtrer les clients",
            description = "Retourne une page de clients correspondant aux critères de recherche : nom du client."
    )
    @GetMapping("/search")
    public PageResponse<ClientResponse> search(
            @ModelAttribute ClientFilterRequest filter,
            Pageable pageable,
            @AuthenticationPrincipal AppUser user
    ) {

        return PageResponse.from(
                clientService.search(
                        filter,
                        pageable,
                        user
                )
        );

    }

    @Operation(
            summary = "Rechercher un client",
            description = "Retourne un client à partir de son identifiant"
    )
    @GetMapping("/{id}")
    public ClientResponse findById(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user) {

        return clientService.findById(id, user);
    }

    @Operation(
            summary = "Modifier un client",
            description = "Met à jour les informations d'un client"
    )
    @PutMapping("/{id}")
    public ClientResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request,
            @AuthenticationPrincipal AppUser user) {

        return clientService.update(id, request, user);
    }

    @Operation(
            summary = "Supprimer un client",
            description = "Supprime un client"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user) {

        clientService.delete(id, user);

        return ResponseEntity.noContent().build();
    }
}