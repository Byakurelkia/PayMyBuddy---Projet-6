package com.paymybuddy.controller;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.User;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Controller
public class MainController {

    private final AccountService accountService;
    private final FriendService friendService;
    private final UserService userService;

    public MainController(AccountService accountService, FriendService friendService, UserService userService) {
        this.accountService = accountService;
        this.friendService = friendService;
        this.userService = userService;
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
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        User userConnected = userService.getUserByEmail(auth);
        model.addAttribute("friendsNumber", friendService.getFriendsListIdByUserId(userConnected.getId()).size());
        Account account = accountService.findAccountByUserId(userConnected.getId());
        BigDecimal bd = new BigDecimal(account.getBalance()).setScale(2, RoundingMode.HALF_UP);
        account.setBalance(bd.doubleValue());
        model.addAttribute("account", account);
        return "home";
    }










}
