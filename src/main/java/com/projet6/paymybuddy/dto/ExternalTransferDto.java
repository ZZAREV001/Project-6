package com.projet6.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExternalTransferDto {

    private Integer id;

    private String ibanUser;

    private BigDecimal amountUser;

    private String emailUser;

    private String description;

    private BigDecimal fees;

    public ExternalTransferDto() {
        super();
    }
    public ExternalTransferDto(String ibanUser, BigDecimal amountUser, String emailUser, String description,
                               BigDecimal fees) {
        super();
        this.ibanUser = ibanUser;
        this.amountUser = amountUser;
        this.emailUser = emailUser;
        this.description = description;
        this.fees = fees;
    }

}
