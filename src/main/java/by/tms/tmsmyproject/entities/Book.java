package by.tms.tmsmyproject.entities;

import by.tms.tmsmyproject.entities.enums.GenreBook;
import by.tms.tmsmyproject.utils.validators.enums.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "books")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends AbstractEntity {

    @Size(min = 1, max = 30, message = "The length of the name should be between 1 and 15")
    private String name;
    @Min(value = -2000, message = "Date incorrect")
    @Max(value = 2022, message = "Date incorrect")
    private Integer year;
    @ValueOfEnum(enumClass = GenreBook.class, message = "Genre of book incorrect")
    private String genreBook;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonBackReference
    private Author author;

    @ToString.Exclude
    @ManyToMany(mappedBy = "books")
    private List<Item> items;

    @NotNull
    @Min(value = 0, message = "Amount is incorrect")
    private Integer amount;

    @NotNull
    @Min(value = 0, message = "Price is incorrect")
    private double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Book book = (Book) o;
        return getId() != null && Objects.equals(getId(), book.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Book{" +
                "name=" + name +
                ", year=" + year +
                ", genreBook=" + genreBook +
                ", author=" + author.getName() + author.getSurname() +
                ", price=" + price + ", id=" + getId() +
                "} ";
    }
}
