package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<Role,Integer> {

    Role findRoleByName(String user);
}
