package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.BankAccountDAO;
import com.projet6.paymybuddy.dao.UserDAO;
import com.projet6.paymybuddy.model.BankAccount;
import com.projet6.paymybuddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BankAccountServiceImplTest {

    @Autowired
    private BankAccountService bankAccountService;

    @MockBean
    private BankAccountDAO bankAccountDAO;

    @MockBean
    private UserDAO userDao;

    private User user;
    private BankAccount bankAccount1;

    private BankAccount bankAccount2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setBalance(BigDecimal.ZERO);
        user.setCreateDate(new Date());

        bankAccount1 = new BankAccount("iban1", "bic1", "bankName1", "user1", user);
        bankAccount2 = new BankAccount("iban2", "bic2", "bankName2", "user2", user);
    }

    @Test
    public void itShouldFindBankAccountByUser() {
        // GIVEN
        List<BankAccount> expectedBankAccounts = Arrays.asList(bankAccount1, bankAccount2);
        when(bankAccountDAO.findBankAccountsByUser_Email(user.getEmail()))
                .thenReturn(expectedBankAccounts);

        // WHEN
        List<BankAccount> foundBankAccounts = bankAccountService
                .findBankAccountByUser(user.getEmail());

        // THEN
        assertThat(foundBankAccounts).isEqualTo(expectedBankAccounts);
        verify(bankAccountDAO, times(1))
                .findBankAccountsByUser_Email(user.getEmail());
    }

    @Test
    public void itShouldAddBankAccount() throws SQLException {
        // GIVEN
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        when(bankAccountDAO.findBankAccountByIban(bankAccount1.getIban())).thenReturn(null);
        when(bankAccountDAO.save(any(BankAccount.class))).thenReturn(bankAccount1);

        // WHEN
        BankAccount addedBankAccount = bankAccountService.addBankAccount(user.getEmail(), bankAccount1);

        // THEN
        assertThat(addedBankAccount).isEqualTo(bankAccount1);
        verify(userDao, times(1)).findByEmail(user.getEmail());
        verify(bankAccountDAO, times(1)).findBankAccountByIban(bankAccount1.getIban());
        verify(bankAccountDAO, times(1)).save(bankAccount1);
    }

    @Test
    public void itShouldDeleteBankAccount() {
        // GIVEN
        when(bankAccountDAO.findById(bankAccount1.getIban())).thenReturn(Optional.of(bankAccount1));

        // WHEN
        Optional<Boolean> result = bankAccountService.deleteBankAccount(bankAccount1.getIban());

        // THEN
        verify(bankAccountDAO, times(1)).findById(bankAccount1.getIban());
        verify(bankAccountDAO, times(1)).delete(bankAccount1);
        assertThat(result).isPresent().contains(true);
    }
}

