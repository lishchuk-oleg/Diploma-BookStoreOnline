package by.tms.tmsmyproject.utils.validators;

import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class UserValidator implements Validator {

    UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        User user = (User) obj;
        String login = user.getLogin();
        String email = user.getEmail();

        if (login != null && userService.isUserLogin(login)) {
            errors.rejectValue("login", "", "This login is already used");
        }
        if (email != null && userService.isUserEmail(email)) {
            errors.rejectValue("email", "", "This email is already used");
        }
    }
}
