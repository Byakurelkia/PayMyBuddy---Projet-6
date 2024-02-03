package com.paymybuddy.service;

import com.paymybuddy.model.Friends;
import com.paymybuddy.model.IdFriend;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.FriendsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final FriendsRepository friendsRepository;
    private final UserService userService;


    public FriendService(FriendsRepository friendsRepository, UserService userService) {
        this.friendsRepository = friendsRepository;
        this.userService = userService;
    }

    //OK
    public IdFriend addFriend(Long userId, Long friendId){
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        return friendsRepository.save(new Friends(new IdFriend(user, friend))).getFriends();
    }

    //OK
    public List<String> getFriendsListByUserId(Long userId){
        return friendsRepository.findByFriendsInitialUserId(userId).stream().map(f-> f.toString()).collect(Collectors.toList());
    }

    public List<Long> getFriendsListIdByUserId(Long userId){
        return friendsRepository.findByFriendsInitialUserId(userId).stream().map(f-> f.getFriends().getFriend().getId()).collect(Collectors.toList());
    }

    //OK
    public void deleteFriend(Long userId, Long friendId){
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        friendsRepository.delete(new Friends(new IdFriend(user, friend)));
    }


}
