package by.tms.tmsmyproject.listener;

import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.enums.RoleUser;
import by.tms.tmsmyproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateUserDefault {

    UserService userService;
    PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdminIfNotExistsAfterStartup() {
        if (!userService.isUserLogin("user")) {
            User user = User.builder()
                    .login("user")
                    .password("user")
                    .role(RoleUser.ROLE_USER.name())
                    .name("Oleg")
                    .surname("Ivanov")
                    .email("1234@mail.ru")
                    .build();
            userService.create(user);
        }
    }
}
