package com.paymybuddy.controller;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.dto.TransferDTO;
import com.paymybuddy.model.*;
import com.paymybuddy.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;
    @MockBean
    BankAccountService bankAccountService;
    @MockBean
    TransactionService transactionService;
    @MockBean
    FriendService friendService;
    @MockBean
    UserService userService;

    @Test
    @WithMockUser("email")
    void shouldReturnTransferView() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        List<User> friendList = List.of(
                new User(2L, "email2", "lastName2", "firstName2", "password2",
                        true, true, true, true),
                new User(3L, "email3", "lastName3", "firstName3", "password3",
                        true, true, true, true)
        );
        List<TransactionDTO> transactionDTOList = List.of(
                new TransactionDTO("firstName3", "description", 10d),
                new TransactionDTO("firstName2", "description", 10d)
        );

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(friendService.getFriendsListAsUserByUserId(1L)).thenReturn(friendList);
        Mockito.when(transactionService.transactionListByUserId(1L)).thenReturn(transactionDTOList);

        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("transactionsList", "friendsList", "friendsListSize"))
                .andExpect(model().attribute("transactionsList", transactionDTOList))
                .andExpect(model().attribute("friendsList", friendList));

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(friendService).getFriendsListAsUserByUserId(1L);
        Mockito.verify(transactionService).transactionListByUserId(1L);
    }


    @Test
    @WithMockUser("email")
    void makePaymentShouldReturnSuccessParam() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        User friend = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        Account sender = new Account(1L, 100, user, null, null);
        Account receiver = new Account(2L, 10, friend, null, null);
        TransferDTO transferDTO = new TransferDTO("description", 10, "friendMail");
        BigDecimal charge = new BigDecimal(10d*5/1000).setScale(2, RoundingMode.HALF_UP);
        Transaction transaction = new Transaction(LocalDateTime.now(), transferDTO.getAmount(),charge.doubleValue(), transferDTO.getDescription(), receiver, sender, TransactionType.TO_FRIEND);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(accountService.getBalanceByUserId(1L)).thenReturn(100d);
        Mockito.when(transactionService.transferToFriend(user, "friendMail", 10d, "description")).thenReturn(transaction);

        mockMvc.perform(post("/makePayment")
                        .with(csrf())
                        .flashAttr("transferInformation", transferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?transferSuccess"))
                .andReturn();

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(accountService, Mockito.times(2)).getBalanceByUserId(1L);
        Mockito.verify(transactionService).transferToFriend(user, "friendMail", 10d, "description");

    }

    @Test
    @WithMockUser("email")
    void makePaymentShouldReturnUnsuccessAmount() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        TransferDTO transferDTO = new TransferDTO("description", 10, "friendMail");

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        mockMvc.perform(post("/makePayment")
                        .with(csrf())
                        .flashAttr("transferInformation", transferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?transferUnsuccessfulAmount"))
                .andReturn();

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(accountService).getBalanceByUserId(1L);
    }

    @Test
    @WithMockUser("email")
    void makePaymentShouldReturnIncorrectAmount() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        TransferDTO transferDTO = new TransferDTO("description", -3, "friendMail");

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        mockMvc.perform(post("/makePayment")
                        .with(csrf())
                        .flashAttr("transferInformation", transferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer?amountIncorrect"))
                .andReturn();

        Mockito.verify(userService).getUserByEmail("email");
    }

    @Test
    @WithMockUser("email")
    void transferToBankAccountShouldReturnSuccessParam() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        Account sender = new Account(1L, 100, user, null, null);
        Transaction transaction = new Transaction(LocalDateTime.now(), 10,0, "description", sender,
                sender, TransactionType.TO_BANK);

        Mockito.when(transactionService.transactionToBankAccount(user)).thenReturn(transaction);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        mockMvc.perform(get("/transferToBankAccount"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?transferToBankAccountSuccess"))
                .andReturn();

        Mockito.verify(transactionService).transactionToBankAccount(user);
        Mockito.verify(userService).getUserByEmail("email");
    }

    @Test
    @WithMockUser("email")
    void transferToBankAccountShouldReturnUnsuccessParam() throws Exception{
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(transactionService.transactionToBankAccount(user)).thenThrow(Exception.class);

        mockMvc.perform(get("/transferToBankAccount"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?transferToBankAccountUnsuccessful"))
                .andReturn();

        Mockito.verify(transactionService).transactionToBankAccount(user);
        Mockito.verify(userService).getUserByEmail("email");
    }

    @Test
    @WithMockUser("email")
    void feedAccountShouldReturnSuccessParam() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        Account sender = new Account(1L, 100, user, null, null);
        Transaction transaction = new Transaction(LocalDateTime.now(), 10d, 0d, "description", sender,
                sender, TransactionType.FROM_BANK);
        double amount = 10d;

        Mockito.when(transactionService.transactionFromBankAccountToAppAccount(user, amount)).thenReturn(transaction);
        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);

        mockMvc.perform(post("/feedAccount")
                        .with(csrf())
                        .flashAttr("amount",amount))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?feedAccountSuccess"))
                .andReturn();

        Mockito.verify(transactionService).transactionFromBankAccountToAppAccount(user,amount);
        Mockito.verify(userService).getUserByEmail("email");

    }

    @Test
    @WithMockUser("email")
    void feedAccountShouldReturnUnsuccessParam() throws Exception {
        double amount = 0d;
        mockMvc.perform(post("/feedAccount")
                        .with(csrf())
                        .flashAttr("amount",amount))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home?feedAccountUnsuccessful"))
                .andReturn();
    }

    @Test
    @WithMockUser("email")
    void updateIBANShouldReturnSuccessParam() throws Exception {
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        String updatedIBAN = "IBANUpdated";
        BankAccount bankAccount = new BankAccount(updatedIBAN,user);

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(bankAccountService.updateBankAccount("email",updatedIBAN)).thenReturn(bankAccount);

        mockMvc.perform(post("/updateIBAN")
                        .with(csrf())
                        .flashAttr("IBAN",updatedIBAN))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?IBANupdatedsuccessfully"))
                .andReturn();

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(bankAccountService).updateBankAccount("email",updatedIBAN);
    }

    @Test
    @WithMockUser("email")
    void updateIBANShouldReturnUnsuccessParam() throws Exception{
        User user = new User(1L, "email", "lastName", "firstName", "password",
                true, true, true, true);
        String updatedIBAN = "IBANUpdated";

        Mockito.when(userService.getUserByEmail("email")).thenReturn(user);
        Mockito.when(bankAccountService.updateBankAccount("email",updatedIBAN)).thenThrow(Exception.class);

        mockMvc.perform(post("/updateIBAN")
                        .with(csrf())
                        .flashAttr("IBAN",updatedIBAN))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?IBANNotUpdated"))
                .andReturn();

        Mockito.verify(userService).getUserByEmail("email");
        Mockito.verify(bankAccountService).updateBankAccount("email",updatedIBAN);
    }


}
