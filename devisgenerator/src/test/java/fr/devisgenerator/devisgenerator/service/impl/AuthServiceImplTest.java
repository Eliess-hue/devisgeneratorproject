package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.dto.request.RegisterRequest;
import fr.devisgenerator.devisgenerator.dto.response.AuthResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.exception.InvalidCredentialsException;
import fr.devisgenerator.devisgenerator.exception.UserAlreadyExistsException;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import fr.devisgenerator.devisgenerator.security.JwtService;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerShouldSucceedWhenUsernameDoesNotExist() {

        // Arrange
        RegisterRequest request =
                new RegisterRequest("johnny", "password123");

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.empty());

        // Act
        authService.register(request);

        // Assert
        verify(userRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    void registerShouldThrowExceptionWhenUsernameAlreadyExists() {

        // Arrange
        RegisterRequest request =
                new RegisterRequest("johnny", "password123");

        AppUser existingUser = AppUser.builder()
                .id(1L)
                .username("johnny")
                .password("encoded-password")
                .role(UserRole.ROLE_USER)
                .build();

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(AppUser.class));

    }

    @Test
    void registerShouldEncodePasswordBeforeSavingUser() {

        // Arrange
        RegisterRequest request =
                new RegisterRequest("johnny", "password123");

        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<AppUser> userCaptor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();

        assertEquals(
                "encoded-password",
                savedUser.getPassword()
        );

        assertNotEquals(
                "password123",
                savedUser.getPassword()
        );

    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {

        // Arrange
        LoginRequest request =
                new LoginRequest("johnny", "password123");

        AppUser user = AppUser.builder()
                .id(1L)
                .username("johnny")
                .password("encoded-password")
                .role(UserRole.ROLE_USER)
                .build();

        when(userRepository.findByUsername("johnny"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"))
                .thenReturn(true);

        when(jwtService.generateToken("johnny"))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals(
                "fake-jwt-token",
                response.token()
        );

        verify(jwtService).generateToken("johnny");

    }

    @Test
    void loginShouldThrowExceptionWhenUsernameDoesNotExist() {

        // Arrange
        LoginRequest request =
                new LoginRequest("john", "password123");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(anyString());
    }

    @Test
    void loginShouldThrowExceptionWhenPasswordIsInvalid() {

        // Arrange
        LoginRequest request =
                new LoginRequest("johnny", "wrong-password");

        AppUser user = AppUser.builder()
                .id(1L)
                .username("johnny")
                .password("encoded-password")
                .role(UserRole.ROLE_USER)
                .build();

        when(userRepository.findByUsername("johnny"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"))
                .thenReturn(false);

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(anyString());
    }

}
