package com.projet6.paymybuddy.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccountDto {

    private String iban;

    private String bic;

    private String bankName;

    private String accountName;
}
