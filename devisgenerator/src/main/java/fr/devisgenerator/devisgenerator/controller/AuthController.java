package fr.devisgenerator.devisgenerator.controller;

import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.dto.request.RegisterRequest;
import fr.devisgenerator.devisgenerator.dto.response.AuthResponse;
import fr.devisgenerator.devisgenerator.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Authentification",
        description = "Endpoints de connexion et inscription"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // REGISTER
    @Operation(
            summary = "Inscription",
            description = "Crée un nouveau compte utilisateur"
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok().build();
    }

    //LOGIN
    @Operation(
            summary = "Connexion",
            description = "Retourne un token JWT"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);

    }

}
