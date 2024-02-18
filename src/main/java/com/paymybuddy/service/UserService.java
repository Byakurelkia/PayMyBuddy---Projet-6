package com.paymybuddy.service;

import com.paymybuddy.ExceptionHandler.CreationUserRequestNonValid;
import com.paymybuddy.ExceptionHandler.MailIsAlreadyUsed;
import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import com.paymybuddy.dto.CreateUserRequest;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final BankAccountService bankAccountService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AccountService accountService, BankAccountService bankAccountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String eMail) throws UsernameNotFoundException {
        log.info("load user by user name started");
        if (!isMailAlreadyExist(eMail)){
            log.error("load user by user name failed, user not found with specified mail");
            throw new UserNotFoundException("User Not Found With Mail Specified ! = "+ eMail);
        }

        return userRepository.findUserByEmail(eMail).get();
    }

    //OK
    public User getUserById(Long userId){
        log.info("get user by id started");
        User user = userRepository.findById(userId).orElseThrow(
                () ->  new UserNotFoundException("User can not find with this id : " + userId));
        return user;
    }

    public User getUserByEmail(String eMail){
        log.info("get user by email started");
        return userRepository.findUserByEmail(eMail).orElseThrow(()-> new UserNotFoundException("User not find with this eMail : " + eMail));
    }

    //OK
    public User createUser(CreateUserRequest request){
        log.info("create user started");
        if (isMailAlreadyExist(request.geteMail())){
            log.error("user can not create, mail is already used by a user!");
            throw new MailIsAlreadyUsed("This mail is already used by a user!");
        }

        if (!createUserRequestIsValid(request)){
            log.error("user can not create, field/s can not be empty or composed only space!");
            throw new CreationUserRequestNonValid("Verify your entry, field/s can not be empty or composed only space!");
        }

        User userToCreate = new User(request.geteMail().trim(), request.getLastName().trim(), request.getFirstName().trim(),
                passwordEncoder.encode(request.getPassword().trim()),true,true,true,true);

        userRepository.save(userToCreate);
        accountService.createAccountForNewUser(userToCreate);
        bankAccountService.createBankAccountForNewUser(request.getIBAN().trim(), userToCreate);

        log.info("User And Account Created On DB - ");
        return userToCreate;
    }

    //OK
    public User updateUser(String userMail, User userUpdateRequest){
        log.info("update user started");
        User actualUser = getUserByEmail(userMail);

        if (isMailAlreadyExist(userUpdateRequest.getEmail())){
            log.error("user can not create, mail is already used by a user!");
            throw new MailIsAlreadyUsed("Mail is already used, please choose an other mail! ");
        }

        actualUser.setFirstName(userUpdateRequest.getFirstName());
        actualUser.setLastName(userUpdateRequest.getLastName());
        actualUser.setEmail(userUpdateRequest.getEmail());
        actualUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));

        return userRepository.save(actualUser);
    }

    //OK
    private boolean isMailAlreadyExist(String eMail){
        log.info("is mail already used? started");
        Optional<User> userFind = userRepository.findUserByEmail(eMail);
        if (userFind.isPresent())
            return true;
        else
            return false;
    }

    //OK
    private boolean createUserRequestIsValid(CreateUserRequest request){
        log.info("create user request is valid? started ");
        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();
        String eMail = request.geteMail().trim();
        String passWord = request.getPassword().trim();
        String IBAN = request.getIBAN().trim();

        if (firstName == null || firstName.length() == 0 || lastName == null || lastName.length() == 0 ||
                eMail == null || eMail.length() == 0 || passWord == null || passWord.length() == 0 || IBAN == null || IBAN.length() == 0)
            return false;
        else
            return true;
    }



}
