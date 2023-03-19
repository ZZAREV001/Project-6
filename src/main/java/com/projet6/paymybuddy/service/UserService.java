package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dto.BuddyFormDto;
import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.model.Relation;
import com.projet6.paymybuddy.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User save(UserRegistrationDto userRegistrationDto);

    User addBuddy(BuddyFormDto buddyFormDto);

    List<Relation> listEmailRelation(String username);

    Boolean deleteBuddy(Integer id);
}
