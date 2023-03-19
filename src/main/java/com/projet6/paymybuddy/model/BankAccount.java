package com.projet6.paymybuddy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "bank_account")
@NoArgsConstructor
@Getter
@Setter
public class BankAccount {
    @Id
    @Column(name = "iban", length = 34)
    private String iban;

    @Column(name = "bic")
    private String bic;

    @Column(name = "bankName")
    private String bankName;

    @Column(name = "accountName")
    private String accountName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bankAccount")
    private List<ExternalTransfer> externalTransfers;

    public BankAccount(String iban, String bic, String bankName, String accountName, User user) {
        this.iban = iban;
        this.bic = bic;
        this.bankName = bankName;
        this.accountName = accountName;
        this.user = user;
    }

    public BankAccount(String iban, String bic, String bankName, String accountName) {
        this.iban = iban;
        this.bic = bic;
        this.bankName = bankName;
        this.accountName = accountName;
    }

}