package com.paymybuddy.service;

import com.paymybuddy.controller.dto.TransactionDTO;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.TransactionType;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final FriendService friendService;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService, FriendService friendService, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.friendService = friendService;
        this.accountService = accountService;
    }

    //OK
    public Transaction transactionToBankAccount(User senderUser) throws Exception {
        log.info("Transaction to bank account started");
        Account senderUserAccount = accountService.findAccountByUserId(senderUser.getId());
        Double amount = senderUserAccount.getBalance();
        if (amount == 0){
            log.error("transaction to ban account failed, amount can not be null");
            throw new Exception("Sold is 0, transfer can not be processed");
        }

        String description = "Transfer from app account to bank account";
        accountService.transferAmountToBankAccount(senderUser);

        Transaction transactionToSave = new Transaction(LocalDateTime.now(), amount, description, senderUserAccount,
                senderUserAccount, TransactionType.TO_BANK);

        return transactionRepository.save(transactionToSave);
    }

    //OK
    public Transaction transactionFromBankAccountToAppAccount(User user, double amount) {
        log.info("transaction from bank account to app account started");
        Account userAccount = accountService.findAccountByUserId(user.getId());
        accountService.addAmountFromBankToAccount(user, amount);
        String description = "Transfer from bank account to application account";

        Transaction transaction = new Transaction(LocalDateTime.now(), amount, description, userAccount, userAccount, TransactionType.FROM_BANK);

        return transactionRepository.save(transaction);
    }

    //OK
    public List<TransactionDTO> transactionListByUserId(Long id) {
        log.info("transaction list by user id started");
        return transactionRepository.findTransactionBySenderUserAccountUserId(id).stream().map(t -> {
            TransactionDTO transactionDTO;
            if (t.getReceiverAccount().getUser().getId() == id) {
                transactionDTO = new TransactionDTO("Me", t.getDescription(), t.getAmount());
            } else {
                transactionDTO = new TransactionDTO(t.getReceiverAccount().getUser().getFirstName(),
                        t.getDescription(), t.getAmount());
            }
            return transactionDTO;
        }).collect(Collectors.toList());
    }

    //OK
    public Transaction transferToFriend(User senderUser, String friendMail, double amount, String description) {
        log.info("transfer to friend started");
        Account senderAccount = accountService.findAccountByUserId(senderUser.getId());
        User receiverUser = userService.getUserByEmail(friendMail);
        Account receiverAccount = accountService.findAccountByUserId(receiverUser.getId());
        accountService.debitAccount(senderUser, amount);
        accountService.creditAccount(receiverUser, amount);
        if (description == null)
            description = "";
        Transaction transaction = new Transaction(LocalDateTime.now(), amount, description, receiverAccount, senderAccount, TransactionType.TO_FRIEND);
        return transactionRepository.save(transaction);
    }

    //OK
    public boolean isFriend(User initialUser, User friendUser) {
        log.info("is friend? started ");
        List<Long> friendListId = friendService.getFriendsListIdByUserId(initialUser.getId());
        if (friendListId.contains(friendUser.getId()))
            return true;
        else
            return false;
    }

}
