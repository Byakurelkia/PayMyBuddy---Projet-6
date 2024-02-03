package com.paymybuddy.controller;


import com.paymybuddy.dto.AuthRequest;
import com.paymybuddy.dto.AuthenticationResponse;
import com.paymybuddy.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    //lorsque tu appuie sur log in de la page login, c'est cette page qui est chargée
    @PostMapping("/home")
    public String loginHome(){
        return "home";
    }

    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/profile")
    public String profile(){
        return "profile";
    }

    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }

    @GetMapping("/transfer")
    public String transfer(){
        return "transfer";
    }



  /*  @ModelAttribute("user")
    public AuthRequest authRequest(){
        return new AuthRequest();
    }

    @PostMapping
    public String authUserRequest(@ModelAttribute("user") AuthRequest authRequest){
        authenticationService.authenticate(authRequest);
        return "redirect:/registration?success";
    }*/








}
