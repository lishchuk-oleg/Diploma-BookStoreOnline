package by.tms.tmsmyproject.entities;

import by.tms.tmsmyproject.entities.enums.RoleUser;
import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import by.tms.tmsmyproject.utils.validators.enums.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {

    @Pattern(regexp = ConstantsRegex.LOGIN, message = "Login does not comply with site rules")
    @Size(min = 3, max = 15, message = "The length of the login should be between 3 and 15")
    private String login;

    private String password;

    @ValueOfEnum(enumClass = RoleUser.class, message = "Role incorrect")
    private String role;

    @Email(message = "Email incorrect")
    private String email;

    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Name incorrect")
    private String name;

    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Surname incorrect")
    private String surname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    private List<Item> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
