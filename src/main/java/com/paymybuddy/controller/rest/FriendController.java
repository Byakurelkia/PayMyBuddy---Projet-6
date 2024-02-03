package com.paymybuddy.controller.rest;

import com.paymybuddy.dto.AddFriendRequest;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller
public class FriendController {

    private final UserService userService;
    private final FriendService friendService;

    public FriendController(UserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }


    @GetMapping("/showFormAddFriend")
    public String showFormAddFriend(Model model, HttpSession session){
        AddFriendRequest addFriendRequest = new AddFriendRequest();
        model.addAttribute("emailFriend",addFriendRequest);
        System.out.println(session.getId() + "     " + session.getAttributeNames().toString() +"             " + session.getServletContext().getAttributeNames().toString());
        System.out.println(session.getServletContext().getContextPath().toString() + "     " + session.getServletContext().getServletContextName() +"     " +
                "        " + session.getServletContext().getServerInfo().toString());
        return "addFriend";
    }

    @PostMapping("/addFriend")
    public String addFriend(@ModelAttribute("emailFriend") AddFriendRequest request ){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails user = (UserDetails) auth.getPrincipal();
            System.out.println("djfhgfjdklsfkjghfjdk  " + "          " + user.getUsername() + "          " + user);
            Long friendId = userService.getUserByEmail(request.getEmailFriend()).getId();
            friendService.addFriend(userService.getUserByEmail(user.getUsername()).getId(), friendId);
        }catch (Exception e){
            e.getMessage();
            System.out.println("user ERXCEPTON : " + e.getMessage());

        }

        return "contact";
    }
}
