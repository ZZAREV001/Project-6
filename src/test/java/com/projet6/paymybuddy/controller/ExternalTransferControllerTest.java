package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.ExternalTransferDto;
import com.projet6.paymybuddy.model.BankAccount;
import com.projet6.paymybuddy.service.BankAccountService;
import com.projet6.paymybuddy.service.TransferService;
import com.projet6.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ExternalTransferController.class)
public class ExternalTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldGetExternalTransferPage() throws Exception {
        mockMvc.perform(get("/user/extransfer"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("externalTransfers", "externalTransfer", "listBankAccount", "bankAccount"))
                .andExpect(view().name("extransfer"));
    }

    // Problem here: test fails if Spring Security is not performed
    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldDoExternalTransfer() throws Exception {
        ExternalTransferDto externalTransferDto = new ExternalTransferDto();
        externalTransferDto.setAmountUser(BigDecimal.valueOf(100));

        // Create a CSRF token for the test request
        CsrfToken csrfToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "test-csrf-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/extransfer/doExternalTransfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("externalTransferDto", externalTransferDto)
                        .sessionAttr("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN", csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/extransfer"));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldAddBankAccount() throws Exception {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("test-iban");
        bankAccount.setBic("test-bic");
        bankAccount.setBankName("test-bank");

        // Create a CSRF token for the test request
        CsrfToken csrfToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "test-csrf-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/extransfer/addBankAccount")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("bankAccount", bankAccount)
                        .sessionAttr("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN", csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/extransfer"));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldDeleteBankAccount() throws Exception {
        String iban = "test-iban";

        // Create a CSRF token for the test request
        CsrfToken csrfToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "test-csrf-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/extransfert/deleteBankAccount")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iban", iban)
                        .sessionAttr("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN", csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/extransfer"));
    }





}
