package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.BankAccountDAO;
import com.projet6.paymybuddy.dao.UserDAO;
import com.projet6.paymybuddy.exception.DataMissingException;
import com.projet6.paymybuddy.model.BankAccount;
import com.projet6.paymybuddy.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountDAO bankAccountDao;

    private final UserDAO userDao;

    @Override
    public List<BankAccount> findBankAccountByUser(String username) {
        return bankAccountDao.findBankAccountsByUser_Email(username);
    }

    @Override
    public BankAccount addBankAccount(String emailOwner, BankAccount bankAccount) {
        if (bankAccount.getIban().isBlank()) {
            throw new DataMissingException("iban cannot be empty");
        }

        User user = userDao.findByEmail(emailOwner);
        String iban = bankAccount.getIban();

        Optional<BankAccount> bankAccountOptional = Optional.ofNullable(bankAccountDao
                .findBankAccountByIban(iban));

        return bankAccountOptional
                .filter(ba -> !ba.getIban().equals(iban))
                .orElseGet(() -> {
                    bankAccount.setUser(user);

                    try {
                        bankAccountDao.save(bankAccount);
                    } catch (Exception e) {
                        try {
                            throw new SQLException("Problem save bank");
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    return bankAccount;
                });
    }

    @Override
    public Optional<Boolean> deleteBankAccount(String iban) {
        return bankAccountDao.findById(iban)
                .map(bankAccount -> {
                    bankAccountDao.delete(bankAccount);
                    return true;
                });
    }


}


