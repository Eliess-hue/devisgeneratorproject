package fr.devisgenerator.devisgenerator.controller;

import fr.devisgenerator.devisgenerator.dto.pdf.GeneratedPdf;
import fr.devisgenerator.devisgenerator.dto.request.QuoteFilterRequest;
import fr.devisgenerator.devisgenerator.dto.request.QuoteLineRequest;
import fr.devisgenerator.devisgenerator.dto.request.QuoteRequest;
import fr.devisgenerator.devisgenerator.dto.response.PageResponse;
import fr.devisgenerator.devisgenerator.dto.response.QuoteResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.service.QuoteEmailService;
import fr.devisgenerator.devisgenerator.service.QuotePdfService;
import fr.devisgenerator.devisgenerator.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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
    private final QuotePdfService quotePdfService;
    private final QuoteEmailService quoteEmailService;

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

    @Operation(
            summary = "Dupliquer un devis",
            description = "Crée un nouveau devis en reprenant le client et les lignes du devis d'origine. Le nouveau devis est créé avec le statut DRAFT"
    )
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<QuoteResponse> duplicate(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quoteService.duplicate(id, user));
    }

    @Operation(
            summary = "Filtrer les devis",
            description = "Retourne une page de devis correspondant aux critères de recherche : texte, statut et période de création."
    )
    @GetMapping("/search")
    public PageResponse<QuoteResponse> search(
            @ModelAttribute QuoteFilterRequest filter,
            Pageable pageable,
            @AuthenticationPrincipal AppUser user
    ) {

        return PageResponse.from(
                quoteService.search(filter, pageable, user)
        );
    }

    @Operation(
            summary = "Générer le PDF d'un devis",
            description = "Génère et retourne le PDF du devis demandé"
    )
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {

        GeneratedPdf pdf = quotePdfService.generatePdf(id, user);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                pdf.filename() +
                                "\"")
                .body(pdf.content());
    }

    @Operation(
            summary = "Envoyer un devis par email",
            description = """
            Envoie le devis du client par email avec le PDF généré en pièce jointe.
            Le destinataire correspond à l'adresse email associée au client du devis.
            Si l'envoi réussit, un devis ayant le statut DRAFT passe automatiquement à PENDING.
            """
    )
    @PostMapping("/{id}/send-email")
    public ResponseEntity<String> sendQuoteEmail(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {

        quoteEmailService.sendQuote(id, user);

        return ResponseEntity.ok("Email envoyé avec succès");
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