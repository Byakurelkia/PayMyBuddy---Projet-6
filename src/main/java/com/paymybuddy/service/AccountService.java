package com.paymybuddy.service;

import com.paymybuddy.ExceptionHandler.AccountNotFoundException;
import com.paymybuddy.ExceptionHandler.BalanceIsNotSuffisant;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccountForNewUser(User user){
        Account accountToCreate = new Account(0, user);
        return accountRepository.save(accountToCreate);
    }

    public Account findAccountByUserId(Long userId){
        return accountRepository.findByUserId(userId).orElseThrow(AccountNotFoundException::new);
    }

    public String addAmountToAccount(User receiverUser, Double amount){
        Account receiverAccount = findAccountByUserId(receiverUser.getId());

        Double newBalanceReceiver = receiverAccount.getBalance() + amount;
        receiverAccount.setBalance(newBalanceReceiver);
        accountRepository.save(receiverAccount);

        return "Amount added successfully ! ";
    }

    public String takeUpAmountFromAccount(User senderUser, Double amount){
        Account senderAccount = findAccountByUserId(senderUser.getId());
        if (!balanceIsSuffisant(senderAccount.getBalance(), amount))
            throw new BalanceIsNotSuffisant("Balance is not suffisant to do this transaction !");

        Double newBalance = senderAccount.getBalance() - amount;
        senderAccount.setBalance(newBalance);
        accountRepository.save(senderAccount);
        return "Amount taken successfully !";
    }

    public double getBalanceByUserId(Long userId){
        return findAccountByUserId(userId).getBalance();
    }

    public void deleteAccount(Long userId){
        Long accountId = findAccountByUserId(userId).getIdAccount();
        accountRepository.deleteById(accountId);
    }

    private boolean balanceIsSuffisant(Double balance, Double amount){
        if (balance>amount)
            return true;
        else
            return false;
    }




}
