package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Slf4j
public class AdminController {

    private final UserMapper userMapper;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("user", userMapper.toDto(CurrentUserUtils.getUser()));
        return "admin/admin-page";
    }

    @GetMapping("/logout")
    public String logoutAdmin() {
        log.debug("user:{} logout",CurrentUserUtils.getLogin());
        return "redirect:/logout";
    }
}
