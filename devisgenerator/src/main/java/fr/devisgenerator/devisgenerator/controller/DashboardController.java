package fr.devisgenerator.devisgenerator.controller;

import fr.devisgenerator.devisgenerator.dto.response.DashboardResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.service.DashboardService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Dashboard",
        description = "Tableau de bord"
)
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Récupérer les données du tableau de bord",
            description = """
            Retourne les statistiques du tableau de bord de l'utilisateur connecté :
            nombre total de devis, devis en attente, devis acceptés,
            chiffre d'affaires total, revenus mensuels et derniers devis créés.
            """)
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal AppUser user
            ) {

        DashboardResponse response =
                dashboardService.getDashboard(user);

        return ResponseEntity.ok(response);

    }

}
