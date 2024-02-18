package com.paymybuddy.controller;

import com.paymybuddy.ExceptionHandler.AccountNotFoundException;
import com.paymybuddy.ExceptionHandler.CreationUserRequestNonValid;
import com.paymybuddy.ExceptionHandler.MailIsAlreadyUsed;
import com.paymybuddy.dto.CreateUserRequest;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;
    @MockBean
    BankAccountService bankAccountService;

    @Test
    @WithMockUser("email")
    void registrationShouldReturnView() throws Exception {
        mockMvc.perform(get("/registration")
                        .flashAttr("user",new CreateUserRequest()))
                .andExpect(view().name("registration"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser("email")
    void registrationShouldRegister() throws Exception {
        CreateUserRequest request = new CreateUserRequest("firstName","lastName","email","password", "IBAN");
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);

        Mockito.when(userService.createUser(request)).thenReturn(user);

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));

        Mockito.verify(userService).createUser(request);
    }

    @Test
    @WithMockUser("email")
    void registrationShouldReturnMailIsAlreadyUsedError() throws Exception {
        CreateUserRequest request = new CreateUserRequest("firstName","lastName","email","password", "IBAN");

        Mockito.when(userService.createUser(request)).thenThrow(MailIsAlreadyUsed.class);

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration?failed"));

        Mockito.verify(userService).createUser(request);
    }

    @Test
    @WithMockUser("email")
    void registrationShouldReturnCreationUserRequestNonValidError() throws Exception {
        CreateUserRequest request = new CreateUserRequest("","","","", "");

        Mockito.when(userService.createUser(request)).thenThrow(CreationUserRequestNonValid.class);

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration?errorFields"));

        Mockito.verify(userService).createUser(request);
    }

    @Test
    @WithMockUser("email")
    void registrationShouldReturnException() throws Exception {
        CreateUserRequest request = new CreateUserRequest("","","","", "");

        Mockito.when(userService.createUser(request)).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration?someErrors"));

        Mockito.verify(userService).createUser(request);
    }

    @Test
    @WithMockUser("emailUp@test.com")
    void updateProfileShouldReturnSuccessParam() throws Exception {
        User user = new User(1L,"emailUp@test.com","lastName","firstName","password",
                true,true,true,true);
        User userToUpdate = new User(1L,"emailTESTSECOND@test.com","lastNameUp","firstNameUp","passwordUp",
                true,true,true,true);

        Mockito.when(userService.isMailValid("emailTESTSECOND@test.com")).thenReturn(Boolean.TRUE);
        Mockito.when(userService.getUserByEmail("emailUp@test.com")).thenReturn(user);
        Mockito.when(userService.updateUser(user.getEmail(), userToUpdate)).thenReturn(userToUpdate);

        mockMvc.perform(post("/updateUserProfile")
                        .with(csrf())
                        .flashAttr("userToUpdate", userToUpdate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?profileUpdated"));

        Mockito.verify(userService).getUserByEmail("emailUp@test.com");
        Mockito.verify(userService).updateUser(user.getEmail(),userToUpdate);

    }

    @Test
    @WithMockUser("emailUp@test.com")
    void updateProfileShouldReturnException() throws Exception {
        User user = new User(1L,"emailUp@test.com","lastName","firstName","password",
                true,true,true,true);

        Mockito.when(userService.isMailValid("emailUp@test.com")).thenReturn(Boolean.TRUE);
        Mockito.when(userService.getUserByEmail("emailUp@test.com")).thenReturn(user);
        Mockito.when(userService.updateUser(user.getEmail(), user)).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/updateUserProfile")
                        .with(csrf())
                        .flashAttr("userToUpdate", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?profileNotUpdated"));

        Mockito.verify(userService).getUserByEmail("emailUp@test.com");
        Mockito.verify(userService).updateUser(user.getEmail(),user);

    }

    @Test
    @WithMockUser("emailUp@test.com")
    void updateProfileShouldReturnMailALreadyExistException() throws Exception {
        User user = new User(1L,"emailUp@test.com","lastName","firstName","password",
                true,true,true,true);

        Mockito.when(userService.isMailValid("emailUp@test.com")).thenReturn(Boolean.TRUE);
        Mockito.when(userService.getUserByEmail("emailUp@test.com")).thenReturn(user);
        Mockito.when(userService.updateUser(user.getEmail(), user)).thenThrow(MailIsAlreadyUsed.class);

        mockMvc.perform(post("/updateUserProfile")
                        .with(csrf())
                        .flashAttr("userToUpdate", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?profileNotUpdated"));

        Mockito.verify(userService).getUserByEmail("emailUp@test.com");
        Mockito.verify(userService).updateUser(user.getEmail(),user);

    }

    @Test
    @WithMockUser("email")
    void profileShouldReturnView() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        BankAccount bankAccount= new BankAccount("IBAN",user);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(bankAccountService.findBankAccountByUserEmail(user.getEmail())).thenReturn(bankAccount);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userBankAccount","user"))
                .andExpect(model().attribute("userBankAccount", bankAccount))
                .andExpect(model().attribute("user", user))
                .andExpect(view().name("profile"));

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(bankAccountService).findBankAccountByUserEmail(user.getEmail());
    }


    @Test
    @WithMockUser("email")
    void profileShouldReturnError() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        BankAccount bankAccount= new BankAccount("IBAN",user);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(bankAccountService.findBankAccountByUserEmail(user.getEmail())).thenThrow(AccountNotFoundException.class);

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?bankAccountNotFound"))
                .andExpect(view().name("redirect:/profile?bankAccountNotFound"));

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(bankAccountService).findBankAccountByUserEmail(user.getEmail());
    }
}
