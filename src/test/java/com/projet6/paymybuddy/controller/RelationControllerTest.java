package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.BuddyFormDto;
import com.projet6.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(RelationController.class)
public class RelationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldAddBuddy_GET() throws Exception {
        mockMvc.perform(get("/user/relation"))
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("relations"));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldAddBuddy_POST() throws Exception {

        mockMvc.perform(post("/user/addBuddy")
                        .param("emailReceiver", "receiver@example.com")
                        .with(csrf())) // Add CSRF token to the request (test failure if not present)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/relation?success"))
                .andExpect(redirectedUrl("/user/relation?success"));

        verify(userService, times(1)).addBuddy(any(BuddyFormDto.class));
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    public void itShouldDeleteBuddy_POST() throws Exception {
        Integer testId = 1;
        when(userService.deleteBuddy(testId)).thenReturn(true); // adjust the return value according to the actual implementation

        mockMvc.perform(post("/user/relation")
                        .param("id", testId.toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/relation"))
                .andExpect(redirectedUrl("/user/relation"));

        verify(userService, times(1)).deleteBuddy(testId);
    }


}

