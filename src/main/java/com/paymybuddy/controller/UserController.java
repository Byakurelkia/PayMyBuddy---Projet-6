package com.paymybuddy.controller;

import com.paymybuddy.ExceptionHandler.AccountNotFoundException;
import com.paymybuddy.ExceptionHandler.CreationUserRequestNonValid;
import com.paymybuddy.ExceptionHandler.MailIsAlreadyUsed;
import com.paymybuddy.dto.CreateUserRequest;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
 public class UserController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    public UserController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    /* REGISTRATION PART - this model is used when a new user will be created and on registration.html page*/
    @ModelAttribute("user")
    public CreateUserRequest createUserRequest(){
        return new CreateUserRequest();
    }

    @ModelAttribute("userToUpdate")
    public User userToUpdate(){
        return new User();
    }

    @ModelAttribute("bankAccount")
    public BankAccount bankAccount(){
        return new BankAccount();
    }

    //retourne la page html registration
    @GetMapping("/registration")
    public String showRegistrationForm(){
        return "registration";
    }

    //OK //quand une requête POST sera faite sur le URI /registration, si c'est success c'est cette méthode qui sera pris en compte
    @PostMapping("/registration")
    public String createUserRequest(@Valid @ModelAttribute("user") CreateUserRequest userRequest){
        log.info("registration method started");
        try{
            userService.createUser(userRequest);
            log.info("registration method successfully made");
            return "redirect:/login?success";
        } catch (MailIsAlreadyUsed m){
            log.info("registration method failed, mail is already used");
            return "redirect:/registration?failed";
        }catch (CreationUserRequestNonValid c){
            log.info("registration method failed, user creation request object is not correct");
            return "redirect:/registration?errorFields";
        }catch(Exception e){
            log.info("registration method failed, other issue..");
            return "redirect:/registration?someErrors";
        }
    }

    //OK //quand tu prends des data = model attribute - quand tu envois des data = model
    @PostMapping("/updateUserProfile")
    public String updateUserProfile(@ModelAttribute("userToUpdate") User userToUpdate) {
        log.info("update user profile started");
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        User userConnected = userService.getUserByEmail(auth);
        try {
            userService.updateUser(userConnected.getEmail(), userToUpdate);
            log.info("update user profile successfully made");
            return "redirect:/login?profileUpdated";
        }catch (Exception e){
            log.error("update user profile failed");
            return "redirect:/profile?profileNotUpdated";
        }

    }

    @GetMapping("/profile")
    public String profile(Model model) throws Exception {
        log.info("profile method started");
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        User userConnected = userService.getUserByEmail(auth);
        try{
            BankAccount account = bankAccountService.findBankAccountByUserEmail(userConnected.getEmail());
            model.addAttribute("userBankAccount", account);
            model.addAttribute("user", userConnected);
            log.info("profile method success");
            return "profile";
        }catch (AccountNotFoundException a){
            log.info("profile method failed, bank account can not find !");
            return "redirect:/profile?bankAccountNotFound";
        }
    }

}
