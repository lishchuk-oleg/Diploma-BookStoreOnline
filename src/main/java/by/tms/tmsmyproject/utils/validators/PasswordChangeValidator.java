package by.tms.tmsmyproject.utils.validators;

import by.tms.tmsmyproject.classes.Password;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class PasswordChangeValidator implements Validator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return Password.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Password password = (Password) obj;
        String oldPassword = password.getOldPassword();
        String newPassword = password.getNewPassword();
        String newPasswordRepeat = password.getNewPasswordRepeat();

        if (!passwordEncoder.matches(oldPassword,CurrentUserUtils.getUser().getPassword())) {
            errors.rejectValue("oldPassword", "", "Existing password is not entered correctly");
        }
        if (!newPassword.equals(newPasswordRepeat)) {
            errors.rejectValue("newPassword", "", "The new password and the repetition of the new password must be equal");
            errors.rejectValue("newPasswordRepeat", "", "The new password and the repetition of the new password must be equal");
        }
    }
}
