package com.paymybuddy.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
@ToString
public class IdFriend  implements Serializable {

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initialUser;

   public IdFriend(User user, User friend) {
      this.friend = friend;
        this.initialUser = user;
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdFriend)) return false;
        IdFriend idFriend = (IdFriend) o;
        return Objects.equals(friend, idFriend.friend) && Objects.equals(initialUser, idFriend.initialUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friend, initialUser);
    }
}
