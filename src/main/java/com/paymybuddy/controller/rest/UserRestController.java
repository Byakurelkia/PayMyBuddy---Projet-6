package com.paymybuddy.controller.rest;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    private final UserService userService;
    private final FriendService friendService;
    private final TransactionService transactionService;

    public UserRestController(UserService userService, FriendService friendService, TransactionService transactionService){
        this.userService = userService;
        this.friendService = friendService;
        this.transactionService = transactionService;
    }

    @GetMapping("/user/{email}")
    public UserDetails helloUser(@PathVariable(value = "email") String email){
        return userService.loadUserByUsername(email);
    }

    @GetMapping("/addNewUser/{id}")
    public boolean getUserByID(@PathVariable(value = "id") Long id, @RequestBody Long friendId) {
        User user = userService.getUserById(id);
        User friend = userService.getUserById(friendId);
         return transactionService.isFriend(user, friend);
    }

    @GetMapping("/transaction")
    public String helloUser(@RequestBody Transaction transaction){
        return null;
    }
}
