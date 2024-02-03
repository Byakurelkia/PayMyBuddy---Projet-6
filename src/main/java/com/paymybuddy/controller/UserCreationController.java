package com.paymybuddy.controller;

import com.paymybuddy.dto.AuthRequest;
import com.paymybuddy.dto.CreateUserRequest;
import com.paymybuddy.model.User;
import com.paymybuddy.security.JwtAuthFilter;
import com.paymybuddy.service.JwtService;
import com.paymybuddy.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;

@Controller
 public class UserCreationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final JwtAuthFilter jwtAuthFilter;

    public UserCreationController(UserService userService, JwtService jwtService, JwtAuthFilter jwtAuthFilter) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /* REGISTRATION PART */
    @ModelAttribute("user")
    public CreateUserRequest createUserRequest(){
        return new CreateUserRequest();
    }

    //retorunera la page html registration
    @GetMapping("/registration")
    public String showRegistrationForm(){
        return "registration";
    }

    //quand une requête POST sera faite sur le URI /registration, si c'est success c'est cette méthode qui sera pris en compte
    @PostMapping("/registration")
    public String createUserRequest(@ModelAttribute("user") CreateUserRequest userRequest){
        userService.createUser(userRequest);
        return "redirect:/login?success";
    }



    /* LOGIN PART */

}
