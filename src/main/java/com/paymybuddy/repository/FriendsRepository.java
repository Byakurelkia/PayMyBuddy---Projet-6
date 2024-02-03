package com.paymybuddy.repository;

import com.paymybuddy.model.Friends;
import com.paymybuddy.model.IdFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendsRepository extends JpaRepository<Friends, IdFriend> {

    List<Friends> findByFriendsInitialUserId(Long user_id);
    List<Friends> findByFriendsFriendId(Long friend_id);
    //save user - lors du transfert on doit pouvoir ajouter l'amie te l'envoyer ainsi son argent

}