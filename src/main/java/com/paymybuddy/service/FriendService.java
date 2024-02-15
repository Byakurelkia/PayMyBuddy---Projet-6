package com.paymybuddy.service;

import com.paymybuddy.model.Friends;
import com.paymybuddy.model.IdFriend;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.FriendsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class FriendService {

    private final FriendsRepository friendsRepository;
    private final UserService userService;


    public FriendService(FriendsRepository friendsRepository, UserService userService) {
        this.friendsRepository = friendsRepository;
        this.userService = userService;
    }

    //OK
    public IdFriend addFriend(Long userId, Long friendId) throws Exception {
        log.info("Add friend started");
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        if (user.getId() == friend.getId()) {
            log.error("Add friend failed, friend and user can not be same person");
            throw new Exception("You can not add yourself as friend!");
        }
        log.info("Add friend successfully made");
        return friendsRepository.save(new Friends(new IdFriend(user, friend))).getFriends();

    }

    //OK
    public List<String> getFriendsListByUserId(Long userId) {
        log.info("Get friend list by user id started");
        return friendsRepository.findByFriendsInitialUserId(userId).stream().map(f -> f.toString()).collect(Collectors.toList());
    }

    public List<User> getFriendsListAsUserByUserId(Long userId) {
        log.info("Get friend list as user by user id started");
        return friendsRepository.findByFriendsInitialUserId(userId).stream().map(f -> {
            User friend = new User(f.getFriends().getFriend().getEmail(), f.getFriends().getFriend().getLastName(),
                    f.getFriends().getFriend().getFirstName());
            return friend;
        }).collect(Collectors.toList());
    }

    public List<Long> getFriendsListIdByUserId(Long userId) {
        log.info("get friends id list by user id started");
        return friendsRepository.findByFriendsInitialUserId(userId).stream().map(f -> f.getFriends().getFriend().getId()).collect(Collectors.toList());
    }

    //OK
    public void deleteFriend(Long userId, Long friendId) {
        log.info("delete friend started");
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        friendsRepository.delete(new Friends(new IdFriend(user, friend)));
    }

    public void deleteFriendByMail(Long userId, String friendMail) {
        log.info("delete friend by friend mail started");
        User user;
        User friend;
        try {
            user = userService.getUserById(userId);
            friend = userService.getUserByEmail(friendMail);
            friendsRepository.delete(new Friends(new IdFriend(user, friend)));
            log.info("delete friend by friend mail success");
        } catch (Exception e) {
            log.error("delete friend by friend mail failed");
        }
    }

}
