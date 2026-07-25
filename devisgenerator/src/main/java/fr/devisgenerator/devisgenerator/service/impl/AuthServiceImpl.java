package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.dto.request.RegisterRequest;
import fr.devisgenerator.devisgenerator.dto.response.AuthResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.exception.InvalidCredentialsException;
import fr.devisgenerator.devisgenerator.exception.UserAlreadyExistsException;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import fr.devisgenerator.devisgenerator.security.JwtService;
import fr.devisgenerator.devisgenerator.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {

        // 1. vérifier si username existe déjà
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Username " + request.username() + " already exists"
            );
        }

        // 2. créer user
        AppUser user = AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.ROLE_USER)
                .build();

        // 3. sauvegarder
        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. trouver user
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid credentials for username "
                                        + request.username()
                        ));

        // 2. vérifier password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(
                    "Invalid credentials for username "
                            + request.username()
            );
        }

        // 3. générer token
        String token = jwtService.generateToken(user.getUsername());

        // 4. retourner
        return new AuthResponse(token);
    }
}