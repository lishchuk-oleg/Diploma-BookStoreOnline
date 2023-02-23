package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.author.AuthorResponseGetDto;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.dto.user.UserRegistrationDto;
import by.tms.tmsmyproject.entities.mapers.*;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class LibraryControllerTest {

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
    @MockBean
    private ItemService itemService;
    @MockBean
    private AuthorService authorService;
    @Spy
    private AuthorMapper authorMapper = MapperResolver.getMapper(AuthorMapperImpl.class);

    private User user;
    private UserRegistrationDto userDto;

    private Book book;
    private BookResponseGetDto bookDto;
    private Page<Book> pageBooks;

    private Author author;
    private AuthorResponseGetDto authorDto;
    private Page<Author> pageAuthors;

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

        userDto = userMapper.toRegistrationDto(user);
        authorDto = authorMapper.toDtoGet(author);
        bookDto = bookMapper.toDtoCreate(book);

        pageAuthors = new PageImpl<>(new ArrayList<>(Arrays.asList(author)));
        pageBooks = new PageImpl<>(new ArrayList<>(Arrays.asList(book)));
    }

    @Test
    @WithMockUser()
    void libraryPageTest() throws Exception {
        mvc.perform(get("/library"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/library"));
    }

    @Test
    @WithMockUser()
    void registrationPageTest() throws Exception {
        mvc.perform(get("/library/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/registration"));
    }

    @Test
    @WithMockUser()
    void loginTest() throws Exception {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getLogin).thenReturn("user");
            mvc.perform(get("/library/login"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/library"));
        }
    }

    @Test
    @WithMockUser()
    void creatTest() throws Exception {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userService.create(user)).thenReturn(user);
        mvc.perform(MockMvcRequestBuilderUtils.postForm("/library/registration", userDto))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser()
    void searchOnPageTest() throws Exception {
        when(bookService.searchBookByAuthorOrNameLikeText(any(String.class), any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/library/search/page/1?text=text"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(view().name("library/books-search-page"));
    }

    @Test
    @WithMockUser()
    void getBooksByGenreTest() throws Exception {
        when(bookService.findBookByGenreOrAll(any(String.class), any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/library/books/page/1?genre=HORROR"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("genre"))
                .andExpect(view().name("library/books-by-genre-page"));
    }

    @Test
    @WithMockUser()
    void getAuthorsByFirstLetterSurnameTest() throws Exception {
        when(authorService.findAuthorByFirstLetterSurnameOrAll(any(String.class), any(Pageable.class))).thenReturn(pageAuthors);
        when(authorMapper.toDtoGet(author)).thenReturn(authorDto);
        mvc.perform(get("/library/authors/page/1?letter=L"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("letter"))
                .andExpect(view().name("library/authors-by-letter-page"));
    }

    @Test
    @WithMockUser()
    void getBooksThisAuthorTest() throws Exception {
        when(authorService.getById(1L)).thenReturn(author);
        when(bookService.findBookByAuthor(any(Author.class), any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/library/authors/1/books/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("author"))
                .andExpect(view().name("library/books-this-author"));
    }

    @Test
    @WithMockUser()
    void returnToAuthorsByFirstLetterTest() throws Exception {
        mvc.perform(get("/library/authors/return"))
                .andExpect(status().is3xxRedirection());
    }
}
