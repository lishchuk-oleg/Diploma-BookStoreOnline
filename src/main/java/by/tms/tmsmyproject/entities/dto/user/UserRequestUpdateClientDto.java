package by.tms.tmsmyproject.entities.dto.user;

import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestUpdateClientDto extends UserDto {

    private Long id;

    private String password;

    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Name incorrect")
    private String name;

    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Surname incorrect")
    private String surname;

}
