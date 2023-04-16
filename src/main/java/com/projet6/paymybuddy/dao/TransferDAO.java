package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferDAO extends JpaRepository<Transfer,Integer> {

}
