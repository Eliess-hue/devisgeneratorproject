package fr.devisgenerator.devisgenerator.controller;

import fr.devisgenerator.devisgenerator.dto.request.QuoteLineRequest;
import fr.devisgenerator.devisgenerator.dto.request.QuoteRequest;
import fr.devisgenerator.devisgenerator.dto.response.QuoteResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
        name = "Devis",
        description = "Gestion des devis"
)
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @Operation(
            summary = "Créer un devis",
            description = "Crée un nouveau devis pour un client"
    )
    @PostMapping
    public ResponseEntity<QuoteResponse> create(
            @Valid @RequestBody QuoteRequest request,
            @AuthenticationPrincipal AppUser user) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quoteService.create(request, user));
    }

    @Operation(
            summary = "Lister les devis",
            description = "Retourne tous les devis de l'utilisateur connecté"
    )
    @GetMapping
    public List<QuoteResponse> findAll(
            @AuthenticationPrincipal AppUser user) {

        return quoteService.findAll(user);
    }

    @Operation(
            summary = "Rechercher un devis",
            description = "Retourne un devis à partir de son identifiant"
    )
    @GetMapping("/{id}")
    public QuoteResponse findById(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user) {

        return quoteService.findById(id, user);
    }

    @Operation(
            summary = "Modifier un devis",
            description = "Met à jour le statut ou le client associé"
    )
    @PutMapping("/{id}")
    public QuoteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody QuoteRequest request,
            @AuthenticationPrincipal AppUser user) {

        return quoteService.update(id, request, user);
    }

    @Operation(
            summary = "Supprimer un devis",
            description = "Supprime un devis"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user) {

        quoteService.delete(id, user);

        return ResponseEntity.noContent().build();
    }

    // Partie Lines

    @Operation(
            summary = "Ajouter une ligne",
            description = "Ajoute une ligne à un devis et recalcule les totaux"
    )
    @PostMapping("/{id}/lines")
    public ResponseEntity<QuoteResponse> addLine(
            @PathVariable Long id,
            @Valid @RequestBody QuoteLineRequest request,
            @AuthenticationPrincipal AppUser user) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        quoteService.addLine(
                                id,
                                request,
                                user
                        )
                );
    }

    @Operation(
            summary = "Modifier une ligne de devis",
            description = "Met à jour une ligne de devis et recalcule les totaux"
    )
    @PutMapping("/{quoteId}/lines/{lineId}")
    public QuoteResponse updateLine(
            @PathVariable Long quoteId,
            @PathVariable Long lineId,
            @Valid @RequestBody QuoteLineRequest request,
            @AuthenticationPrincipal AppUser user) {

        return quoteService.updateLine(
                quoteId,
                lineId,
                request,
                user
        );
    }

    @Operation(
            summary = "Supprimer une ligne",
            description = "Supprime une ligne de devis et recalcule les totaux"
    )
    @DeleteMapping("/{quoteId}/lines/{lineId}")
    public QuoteResponse deleteLine(
            @PathVariable Long quoteId,
            @PathVariable Long lineId,
            @AuthenticationPrincipal AppUser user) {

        return quoteService.deleteLine(
                quoteId,
                lineId,
                user
        );
    }
}