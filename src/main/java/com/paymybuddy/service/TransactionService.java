package com.paymybuddy.service;

import com.paymybuddy.dto.TransactionDTO;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.TransactionType;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        double amount = senderUserAccount.getBalance();
        if (amount == 0){
            log.error("transaction to bank account failed, amount can not be null");
            throw new Exception("Sold is 0, transfer can not be processed");
        }

        String description = "Transfer from app account to bank account";
        accountService.transferAmountToBankAccount(senderUser);

        Transaction transactionToSave = new Transaction(LocalDateTime.now(), amount, 0d, description, senderUserAccount,
                senderUserAccount, TransactionType.TO_BANK);

        return transactionRepository.save(transactionToSave);
    }

    //OK
    public Transaction transactionFromBankAccountToAppAccount(User user, double amount) {
        log.info("transaction from bank account to app account started");
        Account userAccount = accountService.findAccountByUserId(user.getId());
        accountService.addAmountFromBankToAccount(user, amount);
        String description = "Transfer from bank account to application account";

        Transaction transaction = new Transaction(LocalDateTime.now(), amount, 0d, description, userAccount, userAccount, TransactionType.FROM_BANK);

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

        BigDecimal amountWithCharge = new BigDecimal(amount + chargeForAnyTransaction(amount)).setScale(2, RoundingMode.HALF_UP);
        accountService.debitAccount(senderUser, amountWithCharge.doubleValue());
        accountService.creditAccount(receiverUser, amount);

        if (description == null)
            description = "";
        Transaction transaction = new Transaction(LocalDateTime.now(), amount, chargeForAnyTransaction(amount), description, receiverAccount, senderAccount, TransactionType.TO_FRIEND);
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

    //0,5% charge = amount * 0,005
    public double chargeForAnyTransaction(double amount){
        double charge = amount*5/1000;
        BigDecimal bd = new BigDecimal(charge).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
