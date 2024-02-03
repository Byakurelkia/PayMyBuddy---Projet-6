package com.paymybuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Friends {

    @EmbeddedId
    private IdFriend friends;

    public Friends(IdFriend friends) {
        this.friends = friends;
    }
 /* public Friends(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }

    public Friends(User user4) {
    }

*/
    @Override
    public String toString() {
        return "Friends: " + "Firstname: " + friends.getFriend().getFirstName() + " , Lastname: " + friends.getFriend().getLastName();
    }


    public String friendOf() {
        return "Initial User: " + "Firstname: " + friends.getInitialUser().getFirstName() + " , Lastname: " + friends.getInitialUser().getLastName();
    }


}
