package com.paymybuddy.service;

import com.paymybuddy.ExceptionHandler.CreationUserRequestNonValid;
import com.paymybuddy.ExceptionHandler.MailIsAlreadyUsed;
import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import com.paymybuddy.dto.CreateUserRequest;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    AccountService accountService;
    @Mock
    BankAccountService bankAccountService;

    @Test
    public void loadUserByUsernameShouldReturnUser(){
        String email = "test";
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(java.util.Optional.of(user));
        User result = (User) userService.loadUserByUsername(email);

        //one is for function and second is for check mail
        Mockito.verify(userRepository, Mockito.times(2)).findUserByEmail(email);
        assertEquals(user,result);
    }

    @Test
    public void loadUserByUsernameShouldThrowException(){
        String email = "test";

        UserNotFoundException result = assertThrows(UserNotFoundException.class, ()-> userService.loadUserByUsername(email));

        assertEquals("User Not Found With Mail Specified ! = "+ email, result.getMessage());
    }

    @Test
    public void getUserByIdShouldReturnUser(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        User result = userService.getUserById(1L);

        Mockito.verify(userRepository).findById(1L);
        assertEquals(user,result);
    }

    @Test
    public void getUserByIdShouldThrowException(){
        UserNotFoundException result = assertThrows(UserNotFoundException.class, ()-> userService.getUserById(1L));

        assertEquals("User can not find with this id : "+ 1L, result.getMessage());
    }

    @Test
    public void getUserByEmailShouldReturnUser(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        String email = "test";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(java.util.Optional.of(user));
        User result = userService.getUserByEmail(email);

        Mockito.verify(userRepository).findUserByEmail(email);
        assertEquals(user,result);

    }

    @Test
    public void getUserByEmailShouldThrowException(){
        UserNotFoundException result = assertThrows(UserNotFoundException.class,()->userService.getUserByEmail("test"));

        assertEquals("User not find with this eMail : " + "test", result.getMessage());

    }

    @Test
    public void createUserShouldReturnUser(){
        CreateUserRequest request = new CreateUserRequest("firstName","lastName","email","password","IBAN");
        User user = new User("email","lastName","firstName", passwordEncoder.encode("password"),
                true,true,true,true);
        Account account = new Account(0,user);
        BankAccount bankAccount = new BankAccount("IBAN",user);

        Mockito.when(accountService.createAccountForNewUser(user)).thenReturn(account);
        Mockito.when(bankAccountService.createBankAccountForNewUser("IBAN",user)).thenReturn(bankAccount);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(request);

        Mockito.verify(userRepository).save(user);
        assertEquals(user,result);

    }

    @Test
    public void createUserShouldThrowMailExistException(){
        CreateUserRequest request = new CreateUserRequest("firstName","lastName","email","password","IBAN");
        User user = new User("email","lastName","firstName","password",true,true,true,true);

        Mockito.when(userRepository.findUserByEmail("email")).thenReturn(java.util.Optional.of(user));
        MailIsAlreadyUsed result = assertThrows(MailIsAlreadyUsed.class, ()-> userService.createUser(request));

        assertEquals("This mail is already used by a user!",result.getMessage());
    }

    @Test
    public void createUserShouldThrowException(){
        CreateUserRequest request = new CreateUserRequest("","","","","");

        CreationUserRequestNonValid result = assertThrows(CreationUserRequestNonValid.class, ()->
                userService.createUser(request));

        assertEquals("Verify your entry, field/s can not be empty or composed only space!", result.getMessage());
    }

    @Test
    public void updateUserShouldReturnUser(){
        User actualUser = new User("email","lastName","firstName","password",
                true,true,true,true);
        User userUpdated = new User("email2","lastName2","firstName2","password2",
                true,true,true,true);
        String userMail = "email";

        Mockito.when(userRepository.findUserByEmail(userMail)).thenReturn(java.util.Optional.of(actualUser));
        Mockito.when(userRepository.save(userUpdated)).thenReturn(userUpdated);

        User result = userService.updateUser(userMail, userUpdated);

        Mockito.verify(userRepository).findUserByEmail(userMail);
        assertEquals(userUpdated, result);
    }

    @Test
    public void updateUserShouldThrowException(){
        User existUser = new User("email","lastName","firstName","password",
                true,true,true,true);
        User actualUser = new User("email","lastName","firstName","password",
                true,true,true,true);
        String email = "email";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(java.util.Optional.of(existUser));

        MailIsAlreadyUsed result = assertThrows(MailIsAlreadyUsed.class, ()-> userService.updateUser(email, actualUser));

        Mockito.verify(userRepository, Mockito.times(2)).findUserByEmail(email);
        assertEquals("Mail is already used, please choose an other mail! ", result.getMessage());
    }



}
