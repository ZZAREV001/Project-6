package com.projet6.paymybuddy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "relation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"owner", "buddy"})
})
@NoArgsConstructor
@Getter
@Setter
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "owner")
    @ManyToOne
    private User owner;

    @JoinColumn(name = "buddy")
    @ManyToOne
    private User buddy;

    public Relation(User owner, User buddy) {
        this.owner = owner;
        this.buddy = buddy;
    }
}
