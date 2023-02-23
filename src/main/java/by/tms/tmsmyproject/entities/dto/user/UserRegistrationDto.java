package by.tms.tmsmyproject.entities.dto.user;

import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDto extends UserDto{

    @NotBlank
    @Pattern(regexp = ConstantsRegex.LOGIN, message = "Login does not comply with site rules")
    @Size(min = 3, max = 15, message = "The length of the login should be between 3 and 15")
    private String login;
    @NotBlank
    @Pattern(regexp = ConstantsRegex.PASSWORD, message = "Password does not comply with site rules")
    @Size(min = 1, max = 25, message = "The length of the login should be between 5 and 15")
    private String password;

    private String role;
    @NotBlank
    @Email(message = "Email incorrect")
    private String email;
    @NotBlank
    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Name incorrect")
    private String name;
    @NotBlank
    @Pattern(regexp = ConstantsRegex.NAME_USER, message = "Surname incorrect")
    private String surname;

}
