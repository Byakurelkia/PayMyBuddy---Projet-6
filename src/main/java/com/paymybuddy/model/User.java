package com.paymybuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    String email;
    @Column(nullable = false)
    String lastName;
    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String password;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    Set<Friends> friends = new HashSet<>();

    private boolean accountNonExpired;
    private boolean isEnabled;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    public User() {
    }

    public User(String email, String lastName, String firstName, String password, boolean accountNonExpired,
                boolean isEnabled, boolean accountNonLocked, boolean credentialsNonExpired) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
        this.isEnabled = isEnabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public User(String email, String lastName, String firstName) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public User(Long id,String email, String lastName, String firstName, String password, boolean accountNonExpired,
                boolean isEnabled, boolean accountNonLocked, boolean credentialsNonExpired) {
        this.id = id;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
        this.isEnabled = isEnabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    public Set<Friends> getFriends() {
        return friends;
    }

    public void setFriends(Set<Friends> friends) {
        this.friends = friends;
    }

    public void addFriends(Friends friends) {
        this.friends.add(friends);
    }


//    public void deleteFriends(Friends friends) {
//         this.friends.forEach(f-> {
//            if (f.getFriend().getFirstName().equals(friends.getFriend().getFirstName()) &&
//                    f.getFriend().getLastName().equals(friends.getFriend().getLastName())){
//                this.friends.remove(f);
//            }
//        });
//    }


    @Override
    public String toString() {
        return "User{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && email.equals(user.email) && lastName.equals(user.lastName) && firstName.equals(user.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email,password);
    }
}
