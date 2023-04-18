package com.projet6.paymybuddy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return Objects.equals(id, relation.id) &&
                Objects.equals(owner, relation.owner) &&
                Objects.equals(buddy, relation.buddy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, buddy);
    }
}
