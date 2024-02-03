package com.paymybuddy.service;

import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import com.paymybuddy.dto.AuthRequest;
import com.paymybuddy.dto.AuthenticationResponse;
import com.paymybuddy.dto.CreateUserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse createNewUser(CreateUserRequest request){
        userService.createUser(request);
        String token = jwtService.generateToken(request.geteMail());
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticate(AuthRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEMail(), request.getPassword()));
        userService.getUserByEmail(request.getEMail());
        String token = jwtService.generateToken(request.getEMail());
        return AuthenticationResponse.builder().token(token).build();
    }
}
