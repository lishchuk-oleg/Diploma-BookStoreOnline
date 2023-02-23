package by.tms.tmsmyproject.entities;

import by.tms.tmsmyproject.entities.enums.StateItem;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends AbstractEntity {

    private LocalDateTime dateCreate;
    private LocalDateTime dateProgress;
    private LocalDateTime dateChange;
    private LocalDateTime dateExecute;
    private LocalDateTime dateCancelled;

    private Double price;

    @Enumerated(EnumType.STRING)
    private StateItem state;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "item_book",
            joinColumns = @JoinColumn(name="item_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return getId() != null && Objects.equals(getId(), item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
