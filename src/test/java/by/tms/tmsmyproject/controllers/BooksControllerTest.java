package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.dto.book.BookRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.mapers.AuthorMapper;
import by.tms.tmsmyproject.entities.mapers.AuthorMapperImpl;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.entities.mapers.BookMapperImpl;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.validators.BookValidator;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class BooksControllerTest {

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
    private AuthorMapper authorMapper = MapperResolver.getMapper(AuthorMapperImpl.class);
    @MockBean
    private ItemService itemService;
    @MockBean
    private AuthorService authorService;

    private Book book;
    private BookResponseGetDto bookDto;
    private BookRequestCreateDto bookCreateDto;
    private Page<Book> pageBooks;

    private Author author;

    @BeforeEach
    void init() {

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

        bookDto = bookMapper.toDtoCreate(book);
        bookCreateDto = bookMapper.toCreateDto(book);

        bookCreateDto.setAuthor(author);

        pageBooks = new PageImpl<>(new ArrayList<>(Arrays.asList(book)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllBooksTest() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllPageTest() throws Exception {
        when(bookService.getAllPaginated(any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/books/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", hasProperty("content", is(new ArrayList<>(Arrays.asList(bookDto))))))
                .andExpect(view().name("books/all-books-page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBookPageTest() throws Exception {
        when(authorService.getById(1L)).thenReturn(author);
        mvc.perform(get("/books/new?id=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/new"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void creatTest() throws Exception {
        doNothing().when(bookValidator).validate(any(), any());
        mvc.perform(MockMvcRequestBuilderUtils.postForm("/books", bookCreateDto))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void editPageTest() throws Exception {
        when(bookService.getById(1L)).thenReturn(book);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/books/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("object"))
                .andExpect(view().name("books/edit"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void searchBookTest() throws Exception {
        when(bookService.searchBookByNameLikeText(any(String.class),any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/books/search/page/1?text=text"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("genre"))
                .andExpect(model().attribute("list", hasProperty("content", is(new ArrayList<>(Arrays.asList(bookDto))))))
                .andExpect(view().name("books/all-books-page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTest() throws Exception {
        mvc.perform(delete("/books/1"))
                .andExpect(status().is3xxRedirection());
    }
}
