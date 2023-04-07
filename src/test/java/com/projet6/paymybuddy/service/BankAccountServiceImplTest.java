package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.BankAccountDAO;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
public class BankAccountServiceImplTest {

    @Mock
    private BankAccountServiceImpl underTest;

    @Mock
    private BankAccountDAO bankAccountDao;


    @Test
    public void itShouldFindBankAccountByUser () {

    }
}
