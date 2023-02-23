package by.tms.tmsmyproject.classes;

import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Password {
    @NotNull
    private String oldPassword;
    @NotNull
    @Pattern(regexp = ConstantsRegex.PASSWORD, message = "Password does not comply with site rules")
    @Size(min = 5, max = 25, message = "The length of the login should be between 5 and 15")
    private String newPassword;
    @NotNull
    private String newPasswordRepeat;

}
