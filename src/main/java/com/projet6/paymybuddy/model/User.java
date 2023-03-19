package com.projet6.paymybuddy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "createDate")
    private Date createDate;

    @OneToMany(mappedBy = "user")
    private List<BankAccount> bankAccounts;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Relation> relations;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    public User(Integer id, String firstName, String lastName, String email,
                String password, BigDecimal balance, Date createDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.createDate = createDate;
    }

    public User(String firstname, String lastname, String email,
                String password, BigDecimal balance, Date createDate,
                Collection<Role> roles) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.createDate = createDate;
        this.roles = roles;
    }
}
