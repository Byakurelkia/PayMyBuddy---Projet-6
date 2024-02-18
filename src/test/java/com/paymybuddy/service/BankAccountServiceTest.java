package com.paymybuddy.service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {

    @InjectMocks
    private BankAccountService bankAccountService;

    @Mock
    BankAccountRepository bankAccountRepository;

    @Test
    public void createBankAccountForNewUserShouldReturnBankAccount(){
        String IBAN = "IBANTEST";
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        BankAccount bankAccount = new BankAccount(IBAN, user);

        Mockito.when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
        BankAccount result = bankAccountService.createBankAccountForNewUser(IBAN, user);
        Mockito.verify(bankAccountRepository).save(bankAccount);

        assertEquals(bankAccount,result);
    }

    @Test
    public void updateBankAccountShouldReturnBankAccount() throws Exception {
        String userMail = "test";
        String IBAN = "testIBAN";
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        BankAccount bankAccount = new BankAccount(IBAN, user);

        Mockito.when(bankAccountRepository.findBankAccountByUserEmail(userMail))
                .thenReturn(java.util.Optional.of(bankAccount));
        Mockito.when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

        BankAccount result = bankAccountService.updateBankAccount(userMail, IBAN);

        Mockito.verify(bankAccountRepository).findBankAccountByUserEmail(userMail);
        Mockito.verify(bankAccountRepository).save(bankAccount);

        assertEquals(bankAccount, result);

    }

    @Test
    public void updateBankAccountShouldThrowExeption(){
        String userMail = "test";
        String IBAN = "";
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        BankAccount bankAccount = new BankAccount(IBAN, user);

        Mockito.when(bankAccountRepository.findBankAccountByUserEmail(userMail))
                .thenReturn(java.util.Optional.of(bankAccount));
        Exception exception= assertThrows(Exception.class, ()-> bankAccountService.updateBankAccount(userMail, IBAN));

        Mockito.verify(bankAccountRepository).findBankAccountByUserEmail(userMail);
        assertEquals("IBAN can not be empty or composed only spaces!", exception.getMessage());
    }

    @Test
    public void findBankAccountByUserEmailShouldReturnBankAccount() throws Exception {
        String userMail = "test";
        String IBAN = "testIBAN";
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        BankAccount bankAccount = new BankAccount(IBAN, user);

        Mockito.when(bankAccountRepository.findBankAccountByUserEmail(userMail))
                .thenReturn(java.util.Optional.of(bankAccount));
        BankAccount result = bankAccountService.findBankAccountByUserEmail(userMail);

        Mockito.verify(bankAccountRepository).findBankAccountByUserEmail(userMail);
        assertEquals(bankAccount,result);
    }

    @Test
    public void findBankAccountByUserEmailShouldThrowException() throws Exception {
        String userMail = "test";

        Exception result = assertThrows(Exception.class, () -> bankAccountService.findBankAccountByUserEmail(userMail));

        assertEquals("Bank Account Not Find With This User Mail! " + userMail, result.getMessage());
    }
}
