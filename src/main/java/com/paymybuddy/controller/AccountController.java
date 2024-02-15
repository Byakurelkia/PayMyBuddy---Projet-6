package com.paymybuddy.controller;

import com.paymybuddy.controller.dto.TransactionDTO;
import com.paymybuddy.controller.dto.TransferDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.FriendService;
import com.paymybuddy.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class AccountController {

    private final AccountService accountService;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final FriendService friendService;

    public AccountController(AccountService accountService, BankAccountService bankAccountService, TransactionService transactionService, FriendService friendService) {
        this.accountService = accountService;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.friendService = friendService;
    }

    @ModelAttribute("transferInformation")
    public TransferDTO transferDTO(){
        return new TransferDTO();
    }

    //tum modelleri burda vercez friendslist size , friendlist vs gibi
    @GetMapping("/transfer")
    public String transfer(Model model) {
        log.info("transfer method started");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<User> friendsList = friendService.getFriendsListAsUserByUserId(user.getId());
        List<TransactionDTO> transactionsList = transactionService.transactionListByUserId(user.getId());

        model.addAttribute("transactionsList", transactionsList);
        model.addAttribute("friendsList", friendsList);
        model.addAttribute("friendsListSize", friendsList.size());
        log.info("transfer method end");
        return "transfer";
    }


    //OK - aldigim obje ile islem yapip get sayfasina yonlendircem
    @PostMapping("/makePayment")
    public String makePayment(@ModelAttribute("transferInformation") TransferDTO transferDTO){
        log.info("make payment method started");
        System.out.println("amount " + transferDTO.getAmount());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if (accountService.getBalanceByUserId(user.getId()) < transferDTO.getAmount() || accountService.getBalanceByUserId(user.getId()) == 0){
            log.error("make payment method failed,balance is not suffisant");
            return "redirect:/transfer?transferUnsuccessfulAmount";
        }
        try {
            transactionService.transferToFriend(user, transferDTO.getFriendMail(), transferDTO.getAmount(),transferDTO.getDescription());
            log.info("make payment method successfully made");
            return "redirect:/transfer?transferSuccess";
        }catch (Exception e){
            e.getMessage();
            log.error("make payment method failed, other issue");
            return "redirect:/transfer?transferUnsuccessful";
        }
    }


    //OK
    @GetMapping("/transferToBankAccount")
    public String transferToBankAccount(){
        log.info("transfer to bank account started");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        try {
            transactionService.transactionToBankAccount(user);
            log.info("transfer to bank account successfully made");
            return "redirect:/home?transferToBankAccountSuccess";
        }catch (Exception e){
            log.error("transfer to bank account failed");
            return "redirect:/home?transferToBankAccountUnsuccessful";
        }
    }

    //OK
    @PostMapping("/feedAccount")
    public String feedAccount(@ModelAttribute("amount") double amount) throws Exception {
        log.info("feed account from bank account started");
        if (amount<=0){
            log.error("feed account from bank account failed, amount is 0 or negative");
            return "redirect:/home?feedAccountUnsuccessful";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        transactionService.transactionFromBankAccountToAppAccount(user, amount);
        log.info("feed account from bank account successfully made");
        return "redirect:/home?feedAccountSuccess";
    }

    //OK
    @PostMapping("/updateIBAN")
    public String updateIBAN(@ModelAttribute("IBAN") String IBAN) throws Exception {
        log.info("update IBAN method started");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userConnected = (User) auth.getPrincipal();
        try{
            bankAccountService.updateBankAccount(userConnected.getEmail(),IBAN);
            log.info("update IBAN method successfully made");
            return "redirect:/profile?IBANupdatedsuccessfully";
        }catch(Exception e){
            log.error("update IBAN method failed");
            return "redirect:/profile?IBANNotUpdated";
        }
    }

}
