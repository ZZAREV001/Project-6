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
    private BankAccount bankAccount;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setBalance(BigDecimal.ZERO);
        user.setCreateDate(new Date());

        bankAccount = new BankAccount("iban1", "bic1", "bankName1", "user1", user);
    }

    @Test
    public void itShouldAddBankAccount() throws SQLException {
        // GIVEN
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        when(bankAccountDAO.findBankAccountByIban(bankAccount.getIban())).thenReturn(null);
        when(bankAccountDAO.save(any(BankAccount.class))).thenReturn(bankAccount);

        // WHEN
        BankAccount addedBankAccount = bankAccountService.addBankAccount(user.getEmail(), bankAccount);

        // THEN
        assertThat(addedBankAccount).isEqualTo(bankAccount);
        verify(userDao, times(1)).findByEmail(user.getEmail());
        verify(bankAccountDAO, times(1)).findBankAccountByIban(bankAccount.getIban());
        verify(bankAccountDAO, times(1)).save(bankAccount);
    }
}

