package com.projet6.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDto {

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    public UserRegistrationDto(String firstName, String lastname,
                               String password,
                               @Email @NotNull String email) {
        this.firstName = firstName;
        this.lastName = lastname;
        this.password = password;
        this.email = email;
    }
}
