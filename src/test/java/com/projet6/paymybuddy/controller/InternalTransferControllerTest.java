package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.InternalTransferDto;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(SpringExtension.class)
@WebMvcTest(InternalTransferController.class)
public class InternalTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void testInternalTransferPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("internalTransfer"))
                .andExpect(model().attributeExists("transfers", "relations", "internalTransfer"));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void testDoInternalTransfer() throws Exception {
        InternalTransferDto internalTransferDto = new InternalTransferDto();
        internalTransferDto.setAmount(BigDecimal.valueOf(100));
        internalTransferDto.setEmailReceiver("receiver@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/transfer/doInternalTransfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("amount", internalTransferDto.getAmount().toString())
                        .param("emailReceiver", internalTransferDto.getEmailReceiver())
                        .with(csrf())) // Add the CSRF token
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/transfer"));
    }



}
