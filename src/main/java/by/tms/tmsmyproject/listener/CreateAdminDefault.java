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
public class CreateAdminDefault {

    UserService userService;
    PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdminIfNotExistsAfterStartup() {
        if (!userService.isUserWithRole(RoleUser.ROLE_ADMIN.name())) {
            User user = User.builder()
                    .login("admin")
                    .password("admin")
                    .role(RoleUser.ROLE_ADMIN.name())
                    .build();
            userService.create(user);
        }
    }
}
