package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.user.UserResponseGetDto;
import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.entities.mapers.UserMapperImpl;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class AdminControllerTest {

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
    @Spy
    private UserMapper userMapper = MapperResolver.getMapper(UserMapperImpl.class);

    private User user;
    private UserResponseGetDto userDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .login("admin")
                .password("123456")
                .name("Ivan")
                .surname("Ivanov")
                .role("ROLE_ADMIN")
                .email("email@mail.ru")
                .build();

        userDto = userMapper.toDto(user);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminPageTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getUser).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);
            mvc.perform(get("/admin"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("user", hasProperty("login", is("admin"))))
                    .andExpect(view().name("admin/admin-page"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void logoutAdminTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getLogin).thenReturn("user");
            mvc.perform(get("/admin/logout"))
                    .andExpect(status().is3xxRedirection());
        }
    }
}
