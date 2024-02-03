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

import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AccountService accountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String eMail) throws UsernameNotFoundException {
        if (!isMailAlreadyExist(eMail))
            throw new UserNotFoundException("User Not Found With Mail Specified ! = "+ eMail);

        return userRepository.findUserByEmail(eMail).get();
    }

    public User getUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () ->  new UserNotFoundException("User can not find with this id : " + userId));
        return user;
    }

    public User getUserByEmail(String eMail){
        return userRepository.findUserByEmail(eMail).orElseThrow(()-> new UserNotFoundException("User not find with this eMail : " + eMail));
    }

    public User createUser(CreateUserRequest request){
        if (isMailAlreadyExist(request.geteMail()))
            throw new MailIsAlreadyUsed("This mail is already used by a user!");

        if (!createUserRequestIsValid(request))
            throw new CreationUserRequestNonValid("Verify your entry, field/s can not be empty!");

        User userToCreate = new User(request.geteMail(), request.getLastName(), request.getFirstName(),
                passwordEncoder.encode(request.getPassword()),true,true,true,true);

        userRepository.save(userToCreate);
        accountService.createAccountForNewUser(userToCreate);

        log.info("User And Account Created On DB - ");
        return userToCreate;
    }

    private boolean isMailAlreadyExist(String eMail){
        Optional<User> userFind = userRepository.findUserByEmail(eMail);
        if (userFind.isPresent())
            return true;
        else
            return false;
    }

    private boolean createUserRequestIsValid(CreateUserRequest request){
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String eMail = request.geteMail();
        String passWord = request.getPassword();

        if (firstName == null || lastName == null || eMail == null || passWord == null)
            return false;
        else
            return true;
    }

    //essai
    public Long accountNumber(Long idUser){
        return accountService.findAccountByUserId(idUser).getIdAccount();
    }

}
