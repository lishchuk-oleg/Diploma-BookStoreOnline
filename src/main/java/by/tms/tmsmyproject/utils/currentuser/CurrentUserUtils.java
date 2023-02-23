package by.tms.tmsmyproject.utils.currentuser;

import by.tms.tmsmyproject.config.security.CustomUsersDetails;
import by.tms.tmsmyproject.entities.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class CurrentUserUtils {

    public String getLogin() {
        return  getUser().getLogin();
    }

    public Long getId() {
        return getUser().getId();
    }

    public String getLoginInAspect() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return "anonymousUser";
        }
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return "anonymousUser";
        } else {
            return authentication.getName();
        }
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUsersDetails customUsersDetails = (CustomUsersDetails) authentication.getPrincipal();
        return customUsersDetails.getCurrentUser();
    }
}
