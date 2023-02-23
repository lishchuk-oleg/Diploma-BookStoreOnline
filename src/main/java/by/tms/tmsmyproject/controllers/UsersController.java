package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.user.UserRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.user.UserRequestUpdateDto;
import by.tms.tmsmyproject.entities.dto.user.UserResponseGetDto;
import by.tms.tmsmyproject.entities.enums.RoleUser;
import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import by.tms.tmsmyproject.utils.constants.ControllerUtils;
import by.tms.tmsmyproject.utils.validators.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UsersController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    private static int currentPage = 1;
    private static String sizeSortFieldSortDirAsUri = "";
    private static final String EMPTY_PASSWORD = "";

    @ModelAttribute("path")
    public String getPathToPage() {
        return "/users/page";
    }

    @ModelAttribute("uri")
    public String getUri() {
        return "/users";
    }

    @ModelAttribute("allRoles")
    public List<String> allRoles() {
        return RoleUser.ROLES;
    }

    @GetMapping
    public String getAllUsers() {
        return "redirect:/users/page/1";
    }

    @GetMapping("/page/{id}")
    public String getAllPage(@PathVariable(name = "id") Integer id,
                             Model model,
                             @RequestParam(name = "size", defaultValue = "5") Integer size,
                             @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentPage = id;
        PageRequest pageRequest = PageRequest.of(id - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, id, sortField, sortDir, size);
        Page<UserResponseGetDto> page = userService.getAllPaginated(pageRequest).map(userMapper::toDto);
        if (id > 1 && (page.getTotalPages() < id)) {
            return "redirect:/users/page/" + (id - 1) + sizeSortFieldSortDirAsUri;
        }
        model.addAttribute("list", page);
        return "users/all-users-page";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("object") UserRequestCreateDto userDto) {
        return "users/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("object") @Valid UserRequestCreateDto userDto, BindingResult bindingResult) {
        User user = userMapper.toEntity(userDto);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/new";
        }
        userService.create(user);
        return "redirect:/users/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        UserRequestUpdateDto userRequestUpdateDto = userMapper.toAdminUpdateDto(userService.getById(id));
        userRequestUpdateDto.setPassword(EMPTY_PASSWORD);
        model.addAttribute("object", userRequestUpdateDto);
        return "users/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("object") @Valid UserRequestUpdateDto userDto,
                         BindingResult bindingResult) {
        if (!userDto.getPassword().isEmpty() && !userDto.getPassword().matches(ConstantsRegex.PASSWORD)) {
            bindingResult.rejectValue("password", "", "Password incorrect");
        }
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        userService.update(userMapper.toEntity(userDto));
        return "redirect:/users/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/users/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }
}
