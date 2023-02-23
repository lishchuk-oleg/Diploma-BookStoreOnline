package by.tms.tmsmyproject.entities.dto.user;

import by.tms.tmsmyproject.entities.enums.RoleUser;
import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import by.tms.tmsmyproject.utils.validators.enums.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestUpdateDto extends UserDto {

    private Long id;

    private String password;

    @ValueOfEnum(enumClass = RoleUser.class, message = "Role incorrect")
    private String role;

    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Name incorrect")
    private String name;

    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Surname incorrect")
    private String surname;

    private String login;

    private String email;

}
