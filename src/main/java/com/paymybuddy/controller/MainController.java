package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class MainController {

    private final AccountService accountService;
    private final FriendService friendService;

    public MainController(AccountService accountService, FriendService friendService) {
        this.accountService = accountService;
        this.friendService = friendService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //lorsque tu appuie sur log in de la page login, c'est cette page qui est chargée
    @PostMapping("/home")
    public String loginHome(Model model) {
        this.home(model);
        return "home";
    }

    //lorsque tu appuie sur home de la navbar, c'est cette page qui est chargée
    @GetMapping("/home")
    public String home(Model model) {
        log.info("home method started");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userConnected = (User) auth.getPrincipal();
        model.addAttribute("friendsNumber", friendService.getFriendsListIdByUserId(userConnected.getId()).size());
        model.addAttribute("account", accountService.findAccountByUserId(userConnected.getId()));
        return "home";
    }










}
