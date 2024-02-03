package com.paymybuddy.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Embeddable
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

}
