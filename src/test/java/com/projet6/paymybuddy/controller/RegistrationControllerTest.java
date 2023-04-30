package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.model.User;
import com.projet6.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));
    }

    @Test
    public void itShouldRegisterUserAccount_POST() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
        registrationDto.setFirstName("Test");
        registrationDto.setLastName("User");

        User savedUser = new User();
        when(userService.save(any(UserRegistrationDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/registration")
                        .param("email", registrationDto.getEmail())
                        .param("password", registrationDto.getPassword())
                        .param("firstName", registrationDto.getFirstName())
                        .param("lastName", registrationDto.getLastName())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration?success"))
                .andExpect(redirectedUrl("/registration?success"));

        verify(userService, times(1)).save(any(UserRegistrationDto.class));
    }

}
