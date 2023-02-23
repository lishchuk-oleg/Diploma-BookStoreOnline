package by.tms.tmsmyproject.controllers;


import by.tms.tmsmyproject.config.MapperResolver;
import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.dto.author.AuthorRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.author.AuthorRequestUpdateDto;
import by.tms.tmsmyproject.entities.dto.author.AuthorResponseCreateDto;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.mapers.AuthorMapper;
import by.tms.tmsmyproject.entities.mapers.AuthorMapperImpl;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.entities.mapers.BookMapperImpl;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
public class AuthorsControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @MockBean
    private BookService bookService;
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
    private Page<Book> pageBooks;

    private Author author;
    private AuthorRequestUpdateDto authorUpdateDto;
    private AuthorResponseCreateDto authorDto;
    private AuthorRequestCreateDto authorCreateDto;
    private Page<Author> pageAuthors;

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

        authorUpdateDto = authorMapper.toDtoUpdate(author);
        authorCreateDto = authorMapper.toDtoRequestCreate(author);
        authorDto = authorMapper.toDtoCreate(author);
        bookDto = bookMapper.toDtoCreate(book);

        pageAuthors = new PageImpl<>(new ArrayList<>(Arrays.asList(author)));
        pageBooks = new PageImpl<>(new ArrayList<>(Arrays.asList(book)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void newAuthorTest() throws Exception {
        mvc.perform(get("/authors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/new"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllAuthorsTest() throws Exception {
        mvc.perform(get("/authors"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void creatTest() throws Exception {
        when(authorService.create(author)).thenReturn(author);
        mvc.perform(MockMvcRequestBuilderUtils.postForm("/authors", authorCreateDto))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllPageTest() throws Exception {
        when(authorService.getAllPaginated(any(Pageable.class))).thenReturn(pageAuthors);
        when(authorMapper.toDtoCreate(author)).thenReturn(authorDto);
        mvc.perform(get("/authors/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", hasProperty("content", is(new ArrayList<>(Arrays.asList(authorDto))))))
                .andExpect(view().name("authors/all-authors-page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void editTest() throws Exception {
        when(authorService.getById(1L)).thenReturn(author);
        when(authorMapper.toDtoUpdate(author)).thenReturn(authorUpdateDto);
        mvc.perform(get("/authors/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("object"))
                .andExpect(model().attribute("object", hasProperty("id", is(1L))))
                .andExpect(view().name("authors/edit"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void authorBooksTest() throws Exception {
        when(authorService.getById(1L)).thenReturn(author);
        when(bookService.findBookByAuthor(any(Author.class),any(Pageable.class))).thenReturn(pageBooks);
        when(bookMapper.toDtoCreate(book)).thenReturn(bookDto);
        mvc.perform(get("/authors/1/books/page/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", hasProperty("content", is(new ArrayList<>(Arrays.asList(bookDto))))))
                .andExpect(view().name("authors/author-books"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void returnToAllAuthorsTest() throws Exception {
        mvc.perform(get("/authors/return"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void addNewBookThisAuthorPageTest() throws Exception {
        mvc.perform(get("/authors/1/books/new"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void editBookThisAuthorTest() throws Exception {
        mvc.perform(get("/authors/1/books/1/edit"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void searchAuthorTest() throws Exception {
        when(authorService.searchLikeText(any(String.class),any(Pageable.class))).thenReturn(pageAuthors);
        when(authorMapper.toDtoCreate(author)).thenReturn(authorDto);
        mvc.perform(get("/authors/search/page/1?text=text"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attribute("list", hasProperty("content", is(new ArrayList<>(Arrays.asList(authorDto))))))
                .andExpect(view().name("authors/all-authors-page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void returnToSearchResultTest() throws Exception {
        mvc.perform(get("/authors/search/return"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteAuthorTest() throws Exception {
        mvc.perform(delete("/authors/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBookThisAuthorTest() throws Exception {
        mvc.perform(delete("/authors/1/books/1"))
                .andExpect(status().is3xxRedirection());
    }
}
