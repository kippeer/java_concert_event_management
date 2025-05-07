package com.eventmanager.service;

import com.eventmanager.dto.request.LoginRequest;
import com.eventmanager.dto.request.SignupRequest;
import com.eventmanager.dto.response.JwtResponse;
import com.eventmanager.dto.response.MessageResponse;

public interface AuthService {
    
    JwtResponse authenticateUser(LoginRequest loginRequest);
    
    MessageResponse registerUser(SignupRequest signUpRequest);
}