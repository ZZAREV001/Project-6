package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.UserRegistrationDto;
import com.projet6.paymybuddy.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto () {
        return new UserRegistrationDto();
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.save(registrationDto);
        return "redirect:/registration?success";
    }
}
