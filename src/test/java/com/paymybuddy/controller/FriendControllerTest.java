package com.paymybuddy.controller;

import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import com.paymybuddy.model.IdFriend;
import com.paymybuddy.model.User;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.UserService;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(FriendController.class)
public class FriendControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    FriendService friendService;

    @Test
    @WithMockUser("email")
    void contactShouldReturnContactView() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        List<User> friendsList = List.of();

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(friendService.getFriendsListAsUserByUserId(1L)).thenReturn(friendsList);

        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"))
                .andExpect(model().attributeExists("friends", "friendsListSize"))
                .andExpect(model().attribute("friends", friendsList))
                .andExpect(model().attribute("friendsListSize", 0));

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(friendService).getFriendsListAsUserByUserId(1L);
    }

    @Test
    @WithMockUser("email")
    void deleteFriendShouldReturnSuccess() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        String friendMail = "friendMail";

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        mockMvc.perform(get("/deleteFriend/friendMail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact?deletedSuccessfully"));

        Mockito.verify(friendService).deleteFriendByMail(1L,friendMail);

    }

    @Test
    @WithMockUser("email")
    void showFormAddFriendShouldReturnView() throws Exception {
        mockMvc.perform(get("/showFormAddFriend"))
                .andExpect(view().name("addFriend"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("email")
    void addFriendShouldReturnSuccess() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"emailFriend","lastName","firstName","password",
                true,true,true,true);

        String friendMail = "emailFriend";

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.getUserByEmail(friendMail)).thenReturn(friend);
        Mockito.when(friendService.isFriend(1L, friendMail)).thenReturn(Boolean.FALSE);
        Mockito.when(friendService.addFriend(1L,2L)).thenReturn(new IdFriend(user,friend));

        mockMvc.perform(post("/addFriend")
                        .with(csrf())
                        .flashAttr("emailFriend", friendMail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact?friendAddedSuccessfully"));

        Mockito.verify(userService, Mockito.times(2)).getUserByEmail("email");
        Mockito.verify(userService).getUserByEmail(friendMail);
        Mockito.verify(friendService).isFriend(1L,friendMail);
        Mockito.verify(friendService).addFriend(1L,2L);

    }

    @Test
    @WithMockUser("email")
    void addFriendShouldReturnSameUserError() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        String friendMail = "email";

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        mockMvc.perform(post("/addFriend")
                        .with(csrf())
                        .flashAttr("emailFriend", friendMail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/showFormAddFriend?sameUser"));

        Mockito.verify(userService, Mockito.times(2)).getUserByEmail("email");
    }

    @Test
    @WithMockUser("email")
    void addFriendShouldReturnAlreadyExistError() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"emailFriend","lastName","firstName","password",
                true,true,true,true);
        String friendMail = "emailFriend";

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(userService.getUserByEmail(friendMail)).thenReturn(friend);

        Mockito.when(friendService.isFriend(1L, friendMail)).thenReturn(Boolean.TRUE);

        mockMvc.perform(post("/addFriend")
                        .with(csrf())
                        .flashAttr("emailFriend", friendMail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/showFormAddFriend?alreadyExist"));


        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(userService).getUserByEmail(friendMail);
        Mockito.verify(friendService).isFriend(1L,friendMail);
    }

}
