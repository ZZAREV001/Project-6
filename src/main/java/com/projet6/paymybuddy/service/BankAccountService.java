package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.model.BankAccount;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BankAccountService {

    List<BankAccount> findBankAccountByUser(String username);

    BankAccount addBankAccount(String emailOwner, BankAccount bankAccount)
            throws SQLException;

    Optional<Boolean> deleteBankAccount(String iban);
}
