package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.BankAccount;
import com.projet6.paymybuddy.model.User;
import com.projet6.paymybuddy.service.BankAccountService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.projet6.paymybuddy.model")
@Import(BankAccountDAO.class)
public class BankAccountDAOTest {

    @MockBean
    private BankAccountDAO underTest;

    @Autowired
    private TestEntityManager testEntityManager;

    @MockBean
    private UserDAO userDao;

    @Autowired
    private BankAccountService bankAccountService;

    private User user;
    private BankAccount bankAccount;
    private BankAccount bankAccount1;
    private BankAccount bankAccount2;


    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user = testEntityManager.persist(user);

        bankAccount1 = new BankAccount("iban1", "bic1", "bankName1", "user1", user);
        bankAccount2 = new BankAccount("iban2", "bic2", "bankName2", "user2", user);
    }

    @Test
    public void itShouldFindBankAccountsByUser_EmailTest() {
        // GIVEN
        underTest.save(bankAccount1);
        underTest.save(bankAccount2);

        // WHEN
        List<BankAccount> foundBankAccounts = underTest.findBankAccountsByUser_Email(
                user.getEmail());

        // THEN
        assertThat(foundBankAccounts).hasSize(2);
        assertThat(foundBankAccounts).contains(bankAccount1, bankAccount2);
    }


    // Should be in BankAccountServiceImplTest
    @Test
    public void itShouldAddBankAccount() throws SQLException {
        // GIVEN
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        when(underTest.findBankAccountByIban(bankAccount.getIban())).thenReturn(null);
        when(underTest.save(any(BankAccount.class))).thenReturn(bankAccount);

        // WHEN
        BankAccount addedBankAccount = bankAccountService.addBankAccount(user.getEmail(), bankAccount);

        // THEN
        assertThat(addedBankAccount).isEqualTo(bankAccount);
        verify(userDao, times(1)).findByEmail(user.getEmail());
        verify(underTest, times(1)).findBankAccountByIban(bankAccount.getIban());
        verify(underTest, times(1)).save(bankAccount);
    }

}
