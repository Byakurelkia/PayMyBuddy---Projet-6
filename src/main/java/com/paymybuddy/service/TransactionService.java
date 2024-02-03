package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
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

    public Transaction newTransaction(double amount, String senderUserMail, String receiverUserMail, String description){
        User senderUser = userService.getUserByEmail(senderUserMail);
        Account senderUserAccount = accountService.findAccountByUserId(senderUser.getId());

        User receiverUser = userService.getUserByEmail(receiverUserMail);
        Account receiverUserAccount = accountService.findAccountByUserId(receiverUser.getId());

        if (!isFriend(senderUser, receiverUser))
            friendService.addFriend(senderUser.getId(), receiverUser.getId());

        accountService.takeUpAmountFromAccount(senderUser, amount);
        accountService.addAmountToAccount(receiverUser,amount);

        Transaction transactionToSave = new Transaction(LocalDateTime.now(), amount, description, receiverUserAccount,senderUserAccount);
        return transactionRepository.save(transactionToSave);

    }


    //OK
    public boolean isFriend(User initialUser, User friendUser){
        List<Long> friendListId = friendService.getFriendsListIdByUserId(initialUser.getId());
        if (friendListId.contains(friendUser.getId()))
            return true;
        else
            return false;
    }

}
