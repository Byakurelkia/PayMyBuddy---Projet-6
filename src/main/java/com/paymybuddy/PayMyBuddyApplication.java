package com.paymybuddy;

import com.paymybuddy.model.*;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.FriendsRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;

@Slf4j
@SpringBootApplication
@EnableWebMvc
public class PayMyBuddyApplication {
	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}
	/*@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FriendsRepository friendRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;



	@Override
	public void run(String... args) throws Exception {


		User user = userRepository.save(new User("deneme","last","first", encoder.encode("1234"), true,true,true,true));
		User user2 = userRepository.save(new User("deneme2","last2","first2",encoder.encode("1234"),true,true,true,true));
		User user3 = userRepository.save(new User("deneme3","last3","first3",encoder.encode("1234"),true,true,true,true));
		User user4 = userRepository.save(new User("deneme4","last4","first4",encoder.encode("1234"),true,true,true,true));

		Account accountUser1 = new Account(0, user);
		accountRepository.save(accountUser1);
		Account accountUser2 = new Account(0, user2);
		accountRepository.save(accountUser2);
//		Account accountUser3 = new Account(0, user3);
//		accountRepository.save(accountUser3);
		Account accountUser4 = new Account(0, user4);
		accountRepository.save(accountUser4);

		user.addFriends(friendRepository.save(new Friends(new IdFriend(user, user2))));
		user.addFriends(new Friends(new IdFriend(user,user4)));
		user2.addFriends(new Friends(new IdFriend(user2,user4)));
		user2.addFriends(new Friends(new IdFriend(user2,user3)));

		userRepository.save(user);
		userRepository.save(user2);
		userRepository.save(user3);
		userRepository.save(user4);

		Transaction transaction = transactionRepository.save(
				new Transaction(LocalDateTime.now(), 5000, "deneme transaction",
						accountRepository.findByUserId(1L).get(), accountRepository.findByUserId(1L).get(),TransactionType.TO_BANK));

		transactionRepository.save(transaction);

		System.out.println(friendRepository.findByFriendsInitialUserId(1L).toString());
		System.out.println(friendRepository.findByFriendsInitialUserId(2L).toString());
		friendRepository.findByFriendsFriendId(3L).forEach( a ->
				System.out.println("Find by friend id: " + a.friendOf()));


	}*/
}
