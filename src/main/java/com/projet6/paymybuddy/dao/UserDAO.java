package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    User findByEmail(String email);
}
