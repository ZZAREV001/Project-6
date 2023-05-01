package com.projet6.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InternalTransferDto {

    private Integer id;

    private BigDecimal amount;

    private String emailSender;

    private String emailReceiver;

    private String description;
}
