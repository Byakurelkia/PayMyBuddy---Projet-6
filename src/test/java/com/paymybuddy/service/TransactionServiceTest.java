package com.paymybuddy.service;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.TransactionType;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    UserService userService;

    @Mock
    FriendService friendService;

    @Mock
    AccountService accountService;

    @Test
    public void transactionToBankAccountShouldReturnTransaction() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account account = new Account(1L,100,user,null, null);
        String description = "Transfer from app account to bank account";
        Transaction expectedTransaction = new Transaction(LocalDateTime.now(), 100,0d , description , account,
                account, TransactionType.TO_BANK);

        Mockito.when(accountService.findAccountByUserId(1L)).thenReturn(account);
        Mockito.when(accountService.transferAmountToBankAccount(user)).thenReturn("Amount transfered successfully!");
        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);

        Transaction result = transactionService.transactionToBankAccount(user);

        Mockito.verify(accountService).findAccountByUserId(1L);
        Mockito.verify(accountService).transferAmountToBankAccount(user);
        Mockito.verify(transactionRepository).save(expectedTransaction);

        assertEquals(expectedTransaction,result);
    }

    @Test
    public void transactionToBankAccountShouldThrowException() {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account account = new Account(1L,0,user,null, null);

        Mockito.when(accountService.findAccountByUserId(1L)).thenReturn(account);

        Exception result = assertThrows(Exception.class,()-> transactionService.transactionToBankAccount(user));

        Mockito.verify(accountService).findAccountByUserId(1L);
        assertEquals("Sold is 0, transfer can not be processed", result.getMessage());
    }

    @Test
    public void transactionFromBankAccountToAppAccountShouldReturnTransaction(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        Account account = new Account(1L,100,user,null, null);
        String description = "Transfer from bank account to application account";
        double amount = 10d;
        Transaction transaction = new Transaction(LocalDateTime.now(), amount, 0d, description, account, account, TransactionType.FROM_BANK);

        Mockito.when(accountService.findAccountByUserId(1L)).thenReturn(account);
        Mockito.when(accountService.addAmountFromBankToAccount(user, amount)).thenReturn("Amount addedd successfully!");
        Mockito.when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.transactionFromBankAccountToAppAccount(user,amount);

        Mockito.verify(accountService).findAccountByUserId(1L);
        Mockito.verify(accountService).addAmountFromBankToAccount(user,amount);
        assertEquals(transaction,result);


    }

    @Test
    public void transactionListByUserIdShouldReturnTransactionDTOList(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User user2 = new User(2L,"email2","lastName2","firstName2","password",
                true,true,true,true);

        Account account2 = new Account(2L,10,user2,null, null);
        Account account = new Account(1L,10,user,null, null);
        BigDecimal amountWithCharge = new BigDecimal(10d + 10d*5/1000).setScale(2, RoundingMode.HALF_UP);
        List<TransactionDTO> dtoList = List.of(
                new TransactionDTO("Me","description",10d),
                new TransactionDTO("firstName2","description",10d)
        );
        List<Transaction> transactionList = List.of(
                new Transaction(LocalDateTime.now(),10d, 0d,
                "description",account,account,TransactionType.TO_FRIEND),
                new Transaction(LocalDateTime.now(),10d,amountWithCharge.doubleValue(),
                        "description",account2,account,TransactionType.FROM_BANK)
        );

        Mockito.when(transactionRepository.findTransactionBySenderUserAccountUserId(1L)).thenReturn(transactionList);

        List<TransactionDTO> result = transactionService.transactionListByUserId(1L);

        Mockito.verify(transactionRepository).findTransactionBySenderUserAccountUserId(1L);
        assertEquals(dtoList,result);

    }

    @Test
    public void transferToFriendShouldReturnTransaction(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User user2 = new User(2L,"email2","lastName2","firstName2","password",
                true,true,true,true);

        Account senderAccount = new Account(1L,10,user,null, null);
        Account receiverAccount = new Account(2L,10,user2,null, null);

        String receiverMail = "test" ;
        String description = "test";
        double amount = 1d;
        double charge = amount*5/1000;
        BigDecimal bd = new BigDecimal(charge).setScale(2, RoundingMode.HALF_UP);
        double amountWithCharge = amount + bd.doubleValue();
        Transaction expectedTransaction = new Transaction(LocalDateTime.now(), amount, amountWithCharge, description, receiverAccount, senderAccount, TransactionType.TO_FRIEND);

        Mockito.when(accountService.findAccountByUserId(1L)).thenReturn(senderAccount);
        Mockito.when(userService.getUserByEmail(receiverMail)).thenReturn(user2);
        Mockito.when(accountService.findAccountByUserId(2L)).thenReturn(receiverAccount);
        Mockito.when(accountService.debitAccount(user,amountWithCharge)).thenReturn("Amount debited successfully ");
        Mockito.when(accountService.creditAccount(user2,amount)).thenReturn("Amount credited successfully ! ");
        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);

        Transaction result = transactionService.transferToFriend(user, receiverMail, amount, description);

        Mockito.verify(accountService).findAccountByUserId(1L);
        Mockito.verify(userService).getUserByEmail(receiverMail);
        Mockito.verify(accountService).findAccountByUserId(2L);
        Mockito.verify(accountService).debitAccount(user,amountWithCharge);
        Mockito.verify(accountService).creditAccount(user2,amount);
        Mockito.verify(transactionRepository).save(expectedTransaction);
        assertEquals(expectedTransaction,result);


    }

    @Test
    public void isFriendShouldReturnTrue(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User user2 = new User(2L,"email2","lastName2","firstName2","password",
                true,true,true,true);

        List<Long> listLong = List.of(2L);

        Mockito.when(friendService.getFriendsListIdByUserId(1L)).thenReturn(listLong);
        boolean result = transactionService.isFriend(user,user2);

        Mockito.verify(friendService).getFriendsListIdByUserId(1L);
        assertEquals(true, result);
    }

    @Test
    public void isFriendShouldReturnFalse(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User user2 = new User(2L,"email2","lastName2","firstName2","password",
                true,true,true,true);

        List<Long> listLong = List.of(3L);

        Mockito.when(friendService.getFriendsListIdByUserId(1L)).thenReturn(listLong);
        boolean result = transactionService.isFriend(user,user2);

        Mockito.verify(friendService).getFriendsListIdByUserId(1L);
        assertEquals(false, result);
    }

    @Test
    public void chargeForAnyTransactionShouldReturn5PerCentofAmount(){
        double amount = 100d;
        double charge = amount*5/1000;
        BigDecimal bd = new BigDecimal(charge).setScale(2, RoundingMode.HALF_UP);
        double expectedAmount = bd.doubleValue();

        double result = transactionService.chargeForAnyTransaction(amount);

        assertEquals(expectedAmount,result);


    }
}
