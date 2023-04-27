package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.ExternalTransferDto;
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

    // Problem here: test fails
    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void testDoExternalTransfer() throws Exception {
        ExternalTransferDto externalTransferDto = new ExternalTransferDto();
        externalTransferDto.setAmountUser(BigDecimal.valueOf(100));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/extransfer/doExternalTransfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("externalTransferDto", externalTransferDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/extransfer"));
    }
}
