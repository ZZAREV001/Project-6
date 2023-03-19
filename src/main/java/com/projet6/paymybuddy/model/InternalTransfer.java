package com.projet6.paymybuddy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "internal_transfer")
@PrimaryKeyJoinColumn(name = "transfer_id")
@NoArgsConstructor
@Getter
@Setter
public class InternalTransfer extends Transfer {
    @JoinColumn(name = "sender")
    @ManyToOne
    private User userSender;

    @JoinColumn(name = "receiver")
    @ManyToOne
    private User userReceiver;


    public InternalTransfer(User userSender, User userReceiver) {
        super();
        this.userSender = userSender;
        this.userReceiver = userReceiver;
    }

}
