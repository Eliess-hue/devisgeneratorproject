package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.request.LoginRequest;
import fr.devisgenerator.devisgenerator.dto.request.RegisterRequest;
import fr.devisgenerator.devisgenerator.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}