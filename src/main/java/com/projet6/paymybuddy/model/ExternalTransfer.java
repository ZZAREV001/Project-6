package com.projet6.paymybuddy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "external_transfer")
@PrimaryKeyJoinColumn(name = "transfer_id")
@NoArgsConstructor
@Getter
@Setter
public class ExternalTransfer extends Transfer {
    @Column(name = "fees")
    private BigDecimal fees;

    @ManyToOne
    @JoinColumn(name = "bank_account_iban")
    private BankAccount bankAccount;

    public ExternalTransfer(BigDecimal fees, BankAccount bankAccount) {
        super();
        this.fees = fees;
        this.bankAccount = bankAccount;
    }

}
