package com.paymybuddy.service;


import com.paymybuddy.ExceptionHandler.AccountNotFoundException;
import com.paymybuddy.ExceptionHandler.BalanceIsNotSuffisant;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    private Account account;
    private User user;

    @BeforeEach
    void init(){
        user = new User("email","lastName","firstName","password",
                true,true,true,true);
        account = new Account(0,user);
    }

    @Test
    public void createAccountForNewUserShouldReturnAccount(){
        Mockito.when((accountRepository.save(account))).thenReturn(account);
        Account result = accountService.createAccountForNewUser(user);
        Mockito.verify(accountRepository).save(account);
        assertEquals(account.toString(), result.toString());
    }

    @Test
    public void findAccountByUserIdShouldReturnAccount(){
        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        Account result = accountService.findAccountByUserId(1L);
        Mockito.verify(accountRepository).findByUserId(1L);
        assertEquals(account, result);
    }

    @Test
    public void findAccountByUserIdShouldThrowException(){
        Mockito.when(accountRepository.findByUserId(1L)).thenThrow(new AccountNotFoundException());
        String errorMsg = "Account Can Not Find With This User Id..";

        AccountNotFoundException exceptionResult = assertThrows(AccountNotFoundException.class,
                () -> accountService.findAccountByUserId(1L));
        Mockito.verify(accountRepository).findByUserId(1L);
        assertEquals(errorMsg,exceptionResult.getMessage());
    }

    @Test
    public void creditAccountShouldReturnSuccessMessage(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account receiverAccount = new Account(1L,100,user,null, null);

        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.ofNullable(receiverAccount));
        Mockito.when(accountRepository.save(receiverAccount)).thenReturn(receiverAccount);

        String expectedResult = accountService.creditAccount(user,10);

        Mockito.verify(accountRepository).findByUserId(1L);
        Mockito.verify(accountRepository).save(receiverAccount);

        assertEquals("Amount credited successfully ! ", expectedResult);
    }

    @Test
    public void debitAccountShouldReturnSuccessMessage(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account senderAccount = new Account(1L,100,user,null, null);

        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.ofNullable(senderAccount));
        Mockito.when(accountRepository.save(senderAccount)).thenReturn(senderAccount);

        String expectedResult = accountService.debitAccount(user, 10);

        Mockito.verify(accountRepository).findByUserId(1L);
        Mockito.verify(accountRepository).save(senderAccount);

        assertEquals("Amount debited successfully ", expectedResult);

    }

    @Test
    public void debitAccountShouldThrowException(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account senderAccount = new Account(1L,100,user,null, null);

        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.ofNullable(senderAccount));

        Mockito.when(accountService.debitAccount(user, 10))
              .thenThrow(new BalanceIsNotSuffisant("Balance is not suffisant to do this transaction !"));

        BalanceIsNotSuffisant exception = assertThrows(BalanceIsNotSuffisant.class,
                () -> accountService.debitAccount(user,10));

        assertEquals("Balance is not suffisant to do this transaction !", exception.getMessage());
    }

    @Test
    public void addAmountFromBankAccountShouldReturnSuccessMessage(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account senderAccount = new Account(1L,100,user,null, null);

        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.ofNullable(senderAccount));
        Mockito.when(accountRepository.save(senderAccount)).thenReturn(senderAccount);

        String expectedResult = accountService.addAmountFromBankToAccount(user,10);

        Mockito.verify(accountRepository).findByUserId(1L);
        Mockito.verify(accountRepository).save(senderAccount);

        assertEquals("Amount addedd successfully!", expectedResult);

    }

    @Test
    public void transferAmountToBankAccountShouldReturnSuccessMessage(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account senderAccount = new Account(1L,100,user,null, null);

        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.ofNullable(senderAccount));
        Mockito.when(accountRepository.save(senderAccount)).thenReturn(senderAccount);

        String expectedResult = accountService.transferAmountToBankAccount(user);

        Mockito.verify(accountRepository).findByUserId(1L);
        Mockito.verify(accountRepository).save(senderAccount);

        assertEquals("Amount transfered successfully!", expectedResult);
    }

    @Test
    public void getBalanceByUserIdShouldReturnBalance(){
        Account account = new Account(1L,100,user,null, null);

        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        double result = accountService.getBalanceByUserId(1L);
        Mockito.verify(accountRepository).findByUserId(1L);

        assertEquals(100d, result);
    }

    @Test
    public void balanceIsSuffisantShouldReturnTrue(){
        boolean expected = 10<100;
        boolean result = accountService.balanceIsSuffisant(100d,10d);
        assertEquals(expected,result);
    }

    @Test
    public void balanceIsSuffisantShouldReturnFalse(){
        boolean expected = 10>100;
        boolean result = accountService.balanceIsSuffisant(10d,100d);
        assertEquals(expected,result);
    }

}
