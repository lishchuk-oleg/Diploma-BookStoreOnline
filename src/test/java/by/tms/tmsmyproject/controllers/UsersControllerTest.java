package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.user.UserRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.user.UserRequestUpdateDto;
import by.tms.tmsmyproject.entities.dto.user.UserResponseGetDto;
import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.entities.mapers.UserMapperImpl;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.validators.UserValidator;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class UsersControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private BookService bookService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserValidator userValidator;
    @Spy
    private UserMapper userMapper = MapperResolver.getMapper(UserMapperImpl.class);

    private Page<User> page;

    private User user;
    private UserResponseGetDto userDto;
    private UserRequestUpdateDto userUpdateDto;
    private UserRequestCreateDto userCreateDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .login("user")
                .password("123456")
                .name("Ivan")
                .surname("Ivanov")
                .role("ROLE_USER")
                .email("email@mail.ru")
                .build();

        userCreateDto = userMapper.toCreateDto(user);
        userDto = userMapper.toDto(user);
        userUpdateDto = userMapper.toAdminUpdateDto(user);

        page = new PageImpl<>(new ArrayList<>(Arrays.asList(user)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsersTest() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/page/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllPageTest() throws Exception {
        when(userService.getAllPaginated(any(Pageable.class))).thenReturn(page);
        when(userMapper.toDto(user)).thenReturn(userDto);
        mvc.perform(get("/users/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", hasProperty("content", is(new ArrayList<>(Arrays.asList(userDto))))))
                .andExpect(view().name("users/all-users-page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void newUserTest() throws Exception {
        mvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/new"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void creatTest() throws Exception {
        when(userService.create(user)).thenReturn(user);
        mvc.perform(MockMvcRequestBuilderUtils.postForm("/users", userCreateDto))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void editTest() throws Exception {
        when(userService.getById(any())).thenReturn(user);
        when(userMapper.toAdminUpdateDto(any())).thenReturn(userUpdateDto);
        mvc.perform(get("/users/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("object"))
                .andExpect(model().attribute("object", hasProperty("id", is(1L))))
                .andExpect(view().name("users/edit"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().is3xxRedirection());
    }
}
