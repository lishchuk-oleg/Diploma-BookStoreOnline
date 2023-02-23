package by.tms.tmsmyproject.entities;

import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "authors")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Author extends AbstractEntity {

    @Pattern(regexp = ConstantsRegex.NAME_AUTHOR, message = "Name incorrect")
    private String name;
    @Pattern(regexp = ConstantsRegex.NAME_AUTHOR, message = "Surname incorrect")
    private String surname;
    @Min(-2000)
    @Max(2022)
    private Integer birthYear;
    @Min(value = -2000)
    @Max(value = 2022)
    private Integer deathYear;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private List<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Author author = (Author) o;
        return getId() != null && Objects.equals(getId(), author.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
