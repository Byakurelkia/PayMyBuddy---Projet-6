package com.paymybuddy.service;

import com.paymybuddy.ExceptionHandler.AccountNotFoundException;
import com.paymybuddy.ExceptionHandler.BalanceIsNotSuffisant;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //OK
    public Account createAccountForNewUser(User user){
        log.info("create Account For New User started..");
        Account accountToCreate = new Account(0, user);
        return accountRepository.save(accountToCreate);
    }

    //OK
    public Account findAccountByUserId(Long userId){
        log.info("Find Account By User Id started..");
        return accountRepository.findByUserId(userId).orElseThrow(AccountNotFoundException::new);
    }

    //OK
    public String creditAccount(User receiverUser, double amount){
        log.info("Credit Account started..");
        Account receiverAccount = findAccountByUserId(receiverUser.getId());
        double newBalanceReceiver = receiverAccount.getBalance() + amount;
        receiverAccount.setBalance(newBalanceReceiver);
        accountRepository.save(receiverAccount);
        log.info("Credit Account Successfully made..");
        return "Amount credited successfully ! ";
    }

    //OK
    public String  debitAccount(User senderUser, double amount){
        log.info("Debit Account started..");
        Account senderAccount = findAccountByUserId(senderUser.getId());
        if (!balanceIsSuffisant(senderAccount.getBalance(), amount)){
            log.error("Balance is not suffisant.");
            throw new BalanceIsNotSuffisant("Balance is not suffisant to do this transaction !");
        }
        BigDecimal newBalance = new BigDecimal(senderAccount.getBalance() - amount).setScale(2, RoundingMode.HALF_UP);
        //double newBalance =  senderAccount.getBalance() - amount;
        senderAccount.setBalance(newBalance.doubleValue());
        accountRepository.save(senderAccount);
        log.info("Amount debited successfully ");
       return "Amount debited successfully ";
    }

    //OK
    public String addAmountFromBankToAccount(User user, double amount){
        log.info("Add amount from bank account started ");
        Account account = findAccountByUserId(user.getId());
        double balance = account.getBalance() + amount;
        account.setBalance(balance);
        accountRepository.save(account);
        log.info("Amount addedd successfully");
        return "Amount addedd successfully!";
    }

    //OK
    public String transferAmountToBankAccount(User user){
        log.info("Transfer amount from ban account to app account started");
        Account account = findAccountByUserId(user.getId());
        account.setBalance(0);
        accountRepository.save(account);
        log.info("Amount transfered successfully");
        return "Amount transfered successfully!";
    }

    public double getBalanceByUserId(Long userId){
        log.info("Get balance by user id started");
        return findAccountByUserId(userId).getBalance();
    }

    //OK
    public boolean balanceIsSuffisant(Double balance, Double amount){
        log.info("Balance ie suffisant ? started");
        if (balance>=amount)
            return true;
        else
            return false;
    }




}
