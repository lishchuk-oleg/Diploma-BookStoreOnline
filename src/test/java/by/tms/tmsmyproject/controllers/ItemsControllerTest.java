package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.dto.item.ItemResponseGetDto;
import by.tms.tmsmyproject.entities.dto.user.UserResponseGetDto;
import by.tms.tmsmyproject.entities.enums.StateItem;
import by.tms.tmsmyproject.entities.mapers.*;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class ItemsControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @Spy
    private UserMapper userMapper = MapperResolver.getMapper(UserMapperImpl.class);
    @MockBean
    private BookService bookService;
    @Spy
    private BookMapper bookMapper = MapperResolver.getMapper(BookMapperImpl.class);
    @Spy
    private ItemMapper itemMapper = MapperResolver.getMapper(ItemMapperImpl.class);
    @MockBean
    private ItemService itemService;
    @MockBean
    private AuthorService authorService;

    private User user;
    private UserResponseGetDto userDto;

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
        userDto.setId(1L);

        pageItems = new PageImpl<>(new ArrayList<>(Arrays.asList(item)));
        pageBooks = new PageImpl<>(new ArrayList<>(Arrays.asList(book)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getItemsByStateTest() throws Exception {
        when(itemService.findByState(any(String.class), any(Pageable.class))).thenReturn(pageItems);
        when(itemMapper.toGetDto(item)).thenReturn(itemDto);
        mvc.perform(get("/items/page/1?state=CREATE"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(view().name("items/all-items-page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getBooksFromItemTest() throws Exception {
        when(bookService.findBookByItem(any(Long.class), any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        when(itemService.getById(any(Long.class))).thenReturn(item);
        when(itemMapper.toGetDto(item)).thenReturn(itemDto);
        mvc.perform(get("/items/1/books/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(view().name("items/books-in-this-item"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getItemFromUserByIdTest() throws Exception {
        when(itemService.findByUserId(any(Long.class), any(Pageable.class))).thenReturn(pageItems);
        when(itemMapper.toGetDto(item)).thenReturn(itemDto);
        when(userService.getById(any(Long.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        mvc.perform(get("/items/users/1/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(view().name("items/items-this-user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void changeStateTest() throws Exception {
        when(itemService.changeState(any(Long.class),any(String.class))).thenReturn(item);
        mvc.perform(get("/items/1?state=PROGRESS"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBookFromItemTest() throws Exception {
        when(bookService.deleteBookFromItem(any(Long.class),any(Long.class))).thenReturn(item);
        mvc.perform(delete("/items/1/books/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTest() throws Exception {
        when(itemService.deleteById(any(Long.class))).thenReturn(item);
        mvc.perform(delete("/items/1"))
                .andExpect(status().is3xxRedirection());
    }
}
