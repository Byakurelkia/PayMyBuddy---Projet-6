package com.paymybuddy.service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    //OK
    public BankAccount createBankAccountForNewUser(String IBAN, User user){
        log.info("Create Bank account for new user started");
        BankAccount accountToCreate = new BankAccount(IBAN, user);
        bankAccountRepository.save(accountToCreate);
        return accountToCreate;
    }

    public BankAccount updateBankAccount(String userMail,String IBAN) throws Exception {
        log.info("Update Bank account started");
        BankAccount accountToUpdate = findBankAccountByUserEmail(userMail);
        if (IBAN.trim().length() == 0){
            log.error("Update Bank account failed, IBAN can not be null");
            throw new Exception("IBAN can not be empty or composed only spaces!");
        }
        accountToUpdate.setIBAN(IBAN);
        return bankAccountRepository.save(accountToUpdate);
    }

    public BankAccount findBankAccountByUserEmail(String email) throws Exception {
        log.info("Find Bank account By User Email started");
        BankAccount account = bankAccountRepository.findBankAccountByUserEmail(email)
                .orElseThrow(() ->  new Exception("Bank Account Not Find With This User Mail! " + email));
        return account;
    }


}
