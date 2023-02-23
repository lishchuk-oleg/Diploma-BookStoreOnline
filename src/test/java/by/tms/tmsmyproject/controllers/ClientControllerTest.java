package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.dto.item.ItemResponseGetDto;
import by.tms.tmsmyproject.entities.dto.user.UserRequestUpdateClientDto;
import by.tms.tmsmyproject.entities.dto.user.UserResponseGetDto;
import by.tms.tmsmyproject.entities.enums.StateItem;
import by.tms.tmsmyproject.entities.mapers.*;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import by.tms.tmsmyproject.utils.validators.BookValidator;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class ClientControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @MockBean
    private BookService bookService;
    @MockBean
    private BookValidator bookValidator;
    @Spy
    private BookMapper bookMapper = MapperResolver.getMapper(BookMapperImpl.class);
    @Spy
    private ItemMapper itemMapper = MapperResolver.getMapper(ItemMapperImpl.class);
    @Spy
    private UserMapper userMapper = MapperResolver.getMapper(UserMapperImpl.class);
    @MockBean
    private ItemService itemService;
    @MockBean
    private AuthorService authorService;

    private User user;
    private UserResponseGetDto userDto;
    private UserRequestUpdateClientDto userClientDto;

    private Book book;
    private BookResponseGetDto bookDto;
    private Page<Book> pageBooks;

    private Author author;

    private Item item;
    private ItemResponseGetDto itemDto;
    private Page<Item> pageItems;

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

        author = Author.builder()
                .id(1L)
                .name("Ray")
                .surname("Bradbury")
                .birthYear(1920)
                .deathYear(2012)
                .build();

        book = Book.builder()
                .id(1L)
                .name("The Martian Chronicles")
                .genreBook("FICTION")
                .amount(10)
                .price(50)
                .year(1950)
                .author(author)
                .build();

        item = Item.builder()
                .id(1L)
                .state(StateItem.CREATE)
                .books(new ArrayList<>(Arrays.asList(book)))
                .price(10d)
                .user(user)
                .build();

        itemDto = itemMapper.toGetDto(item);
        bookDto = bookMapper.toDtoCreate(book);
        userDto = userMapper.toDto(user);
        userClientDto = userMapper.toClientUpdateDto(user);
        userDto.setId(1L);

        pageItems = new PageImpl<>(new ArrayList<>(Arrays.asList(item)));
        pageBooks = new PageImpl<>(new ArrayList<>(Arrays.asList(book)));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void clientPageTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getUser).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);
            mvc.perform(get("/client"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().attribute("user", hasProperty("login", is("user"))))
                    .andExpect(view().name("client/client-page"));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void logoutClientTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getLogin).thenReturn("user");
            mvc.perform(get("/client/logout"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/logout"));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void changeClientDataPageTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getUser).thenReturn(user);
            when(userMapper.toClientUpdateDto(user)).thenReturn(userClientDto);
            mvc.perform(get("/client/edit/data"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("object"))
                    .andExpect(view().name("client/edit-data"));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void changeClientPasswordPageTest() throws Exception {
        mvc.perform(get("/client/edit/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/edit-password"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void addBookToCartTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getLogin).thenReturn("user");
            when(bookService.changeAmountDownward(1L)).thenReturn(true);
            when(bookService.getById(1L)).thenReturn(book);
            when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
            mvc.perform(get("/client/books/1/cart"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCartTest() throws Exception {
        mvc.perform(get("/client/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/cart"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getLogin).thenReturn("user");
            mvc.perform(delete("/client/cart/1"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void clearCartTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getLogin).thenReturn("user");
            mvc.perform(get("/client/cart/clear"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/client"));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getItemByStateTest() throws Exception {
        when(itemService.findByStateAndUserId(any(String.class), any(Long.class), any(Pageable.class))).thenReturn(pageItems);
        when(itemMapper.toGetDto(item)).thenReturn(itemDto);
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getId).thenReturn(1L);
            mvc.perform(get("/client/item/page/1?state=CREATE"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("list"))
                    .andExpect(view().name("client/items-by-state"));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBooksFromItemTest() throws Exception {
        when(bookService.findBookByItemAndUserId(any(Long.class), any(Long.class), any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        when(itemService.getById(any(Long.class))).thenReturn(item);
        when(itemMapper.toGetDto(item)).thenReturn(itemDto);
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getId).thenReturn(1L);
            mvc.perform(get("/client/item/1/books/page/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("list"))
                    .andExpect(view().name("client/books-in-this-item"));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void changeStateTest() throws Exception {
        when(itemService.getById(any(Long.class))).thenReturn(item);
        when(itemService.changeState(any(Long.class),any(String.class))).thenReturn(item);
        mvc.perform(get("/client/item/1?state=CHANGING"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteBookFromItemTest() throws Exception {
            mvc.perform(delete("/client/item/1/books/1"))
                    .andExpect(status().is3xxRedirection());
    }
}
