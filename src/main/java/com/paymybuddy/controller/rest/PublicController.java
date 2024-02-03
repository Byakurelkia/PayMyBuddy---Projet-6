package com.paymybuddy.controller.rest;

import com.paymybuddy.dto.AuthRequest;
import com.paymybuddy.dto.AuthenticationResponse;
import com.paymybuddy.dto.CreateUserRequest;
import com.paymybuddy.model.User;
import com.paymybuddy.service.AuthenticationService;
import com.paymybuddy.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public PublicController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }


    @PostMapping("/addNewUser")
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/auth/generateToken")
    public AuthenticationResponse generateToken(@RequestBody AuthRequest request){
        return authenticationService.authenticate(request);
    }
}
