package com.paymybuddy.controller;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.User;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;
    @MockBean
    FriendService friendService;
    @MockBean
    UserService userService;

    @Test
    @WithMockUser("email")
    void shouldReturnHomeViewWithModels() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account account = new Account(1L,100,user,null, null);

        List<Long> listFriendsAsLong = List.of(2L,3L);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(friendService.getFriendsListIdByUserId(1L)).thenReturn(listFriendsAsLong);
        Mockito.when(accountService.findAccountByUserId(1L)).thenReturn(account);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("friendsNumber", "account"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attribute("friendsNumber", 2));

        Mockito.verify(friendService).getFriendsListIdByUserId(1L);
        Mockito.verify(accountService).findAccountByUserId(1L);

    }

    @Test
    @WithMockUser("email")
    void postMethodShouldReturnHomeViewWithModels() throws Exception {
       mockMvc.perform(formLogin().user("email").password("password")).andExpect(authenticated());
    }

    @Test
    void postMethodShouldReturnUnauthenticated() throws Exception {
        mockMvc.perform(formLogin().user("email").password("password")).andExpect(unauthenticated());
    }

    @Test
    void getMethodShouldReturnLoginForm() throws Exception {
        MvcResult result = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(result.getRequest().getRequestURI(), "/login");
    }
}
