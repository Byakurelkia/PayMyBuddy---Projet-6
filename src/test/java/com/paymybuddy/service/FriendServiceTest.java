package com.paymybuddy.service;


import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import com.paymybuddy.model.Friends;
import com.paymybuddy.model.IdFriend;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.FriendsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;

    @Mock
    FriendsRepository friendsRepository;

    @Mock
    UserService userService;

    @Test
    public void addFriendShouldReturnIdFriend() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true);
        IdFriend idFriend = new IdFriend(user, friend);
        Friends friends = new Friends(idFriend);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(userService.getUserById(2L)).thenReturn(friend);
        Mockito.when(friendsRepository.save(friends)).thenReturn(friends);

        IdFriend result = friendService.addFriend(1L,2L);
        Mockito.verify(userService).getUserById(1L);
        Mockito.verify(userService).getUserById(2L);

        assertEquals(idFriend, result);


    }

    @Test
    public void addFriendShouldThrowException(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        IdFriend idFriend = new IdFriend(user, user);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Exception result = assertThrows(Exception.class, ()-> friendService.addFriend(1L,1L));
        Mockito.verify(userService,Mockito.times(2)).getUserById(1L);

        assertEquals("You can not add yourself as friend!", result.getMessage());
    }

    @Test
    public void getFriendsListAsUserByUserIdShouldReturnFriendsAsUserList(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true);
        IdFriend idFriend = new IdFriend(user, friend);
        List<Friends> listFriends = List.of(new Friends(idFriend));
        List<User> listFriendsAsUser = List.of(new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true));

        Mockito.when(friendsRepository.findByFriendsInitialUserId(1L)).thenReturn(listFriends);

        List<User> result = friendService.getFriendsListAsUserByUserId(1L);

        Mockito.verify(friendsRepository).findByFriendsInitialUserId(1L);
        assertEquals(listFriendsAsUser.toString(), result.toString());
    }

    @Test
    public void getFriendsListIdByUserIdShouldReturnListLong(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true);
        IdFriend idFriend = new IdFriend(user, friend);
        List<Friends> listFriends = List.of(new Friends(idFriend));
        List<Long> listFriendsAsLong = List.of(2L);

        Mockito.when(friendsRepository.findByFriendsInitialUserId(1L)).thenReturn(listFriends);

        List<Long> result = friendService.getFriendsListIdByUserId(1L);

        Mockito.verify(friendsRepository).findByFriendsInitialUserId(1L);
        assertEquals(listFriendsAsLong, result);
    }

    @Test
    public void deleteFriendByMailShouldReturnNothing() throws Exception {
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true);
        String mailFriend = "email2";
        IdFriend idFriend = new IdFriend(user, friend);
        Friends friends = new Friends(idFriend);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(userService.getUserByEmail(mailFriend)).thenReturn(friend);

        friendService.deleteFriendByMail(1L,mailFriend);

        Mockito.verify(friendsRepository).delete(friends);
    }

    @Test
    public void deleteFriendByMailShouldThrowException(){
        Mockito.when(userService.getUserById(1L)).thenThrow(UserNotFoundException.class);
        Exception exception = assertThrows(Exception.class, ()-> friendService.deleteFriendByMail(1L,"test"));
        assertEquals("Error during delete....", exception.getMessage());
    }

    @Test
    public void isFriendShouldReturnTrue(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true);
        String mailFriend = "email2";
        IdFriend idFriend = new IdFriend(user, friend);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(userService.getUserByEmail(mailFriend)).thenReturn(friend);
        Mockito.when(friendsRepository.existsById(idFriend)).thenReturn(true);

        boolean result = friendService.isFriend(1L,mailFriend);

        Mockito.verify(userService).getUserById(1L);
        Mockito.verify(userService).getUserByEmail(mailFriend);
        Mockito.verify(friendsRepository).existsById(idFriend);

        assertEquals(true, result);
    }

    @Test
    public void isFriendShouldReturnFalse(){
        User user = new User(1L,"email","lastName","firstName","password",
                true,true,true,true);
        User friend = new User(2L,"email2","lastName2","firstName2","password2",
                true,true,true,true);
        String mailFriend = "email2";
        IdFriend idFriend = new IdFriend(user, friend);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(userService.getUserByEmail(mailFriend)).thenReturn(friend);
        Mockito.when(friendsRepository.existsById(idFriend)).thenReturn(false);

        boolean result = friendService.isFriend(1L,mailFriend);

        Mockito.verify(userService).getUserById(1L);
        Mockito.verify(userService).getUserByEmail(mailFriend);
        Mockito.verify(friendsRepository).existsById(idFriend);

        assertEquals(false, result);
    }
}

