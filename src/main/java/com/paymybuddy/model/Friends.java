package com.paymybuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friends)) return false;
        Friends friends1 = (Friends) o;
        return Objects.equals(friends, friends1.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friends);
    }
}
