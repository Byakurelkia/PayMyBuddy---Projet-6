package com.paymybuddy.controller;

import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.FriendsRepository;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class FriendController {

    private final UserService userService;
    private final FriendService friendService;
    private final FriendsRepository friendsRepository;

    public FriendController(UserService userService, FriendService friendService, FriendsRepository friendsRepository) {
        this.userService = userService;
        this.friendService = friendService;
        this.friendsRepository = friendsRepository;
    }

    //OK
    @GetMapping("/contact")
    public String contact(Model model) {
        log.info("contact method started");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userConnected = (User) auth.getPrincipal();
        List<User> friendsList = friendService.getFriendsListAsUserByUserId(userConnected.getId());
        int friendsListSize = friendsList.size();
        model.addAttribute("friends", friendsList);
        model.addAttribute("friendsListSize", friendsListSize);
        return "contact";
    }

    //OK
    @GetMapping("/deleteFriend/{friendEmail}")
    public String deleteFriend(@PathVariable(name = "friendEmail") String email){
        log.info("delete friend method started");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        try{
            friendService.deleteFriendByMail(user.getId(), email);
            log.info("delete friend method successfully made");
            return "redirect:/contact?deletedSuccessfully";
        }catch (UserNotFoundException u){
            log.error("delete friend method failed");
            return "redirect:/contact?deleteFailed";
        }
    }

    //OK
    @GetMapping("/showFormAddFriend")
    public String showFormAddFriend(){
        return "addFriend";
    }

    //OK
    @PostMapping("/addFriend")
    public String addFriend(@ModelAttribute("emailFriend") String request){
        log.info("add friend method started");
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            Long friendId = userService.getUserByEmail(request).getId();
            if (user.getId() == friendId){
                log.error("add friend failed, friend user is same user");
                return "redirect:/showFormAddFriend?sameUser";
            }
            friendService.addFriend(userService.getUserByEmail(user.getUsername()).getId(), friendId);
            log.info("add friend method successfully made");
            return "redirect:/contact?friendAddedSuccessfully";
        }catch (Exception e){
            e.getMessage();
            log.error("add friend failed, other issue...");
            return "redirect:/showFormAddFriend?addFriendUnsuccessful";
        }
    }
}
