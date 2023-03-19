package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountDAO extends JpaRepository<BankAccount, String> {

    List<BankAccount> findBankAccountsByUser_Email(String username);

    BankAccount findBankAccountByIbanAndUser_Email(String ibanUser, String emailUser);

    BankAccount findBankAccountByIban(String iban);
}
