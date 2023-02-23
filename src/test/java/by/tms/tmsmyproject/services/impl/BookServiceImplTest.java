package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.book.BookRequestCreateDto;
import by.tms.tmsmyproject.entities.enums.StateItem;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.repositories.BookRepository;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    AuthorService authorService;
    @Mock
    ItemService itemService;
    @Mock
    BookMapper bookMapper;
    @InjectMocks
    BookServiceImpl bookService;

    private static Author author;
    private static Item item;
    private static Book book;
    private static Page<Book> page;
    private static PageRequest pageRequest;
    private static Book bookAnother;
    private static BookRequestCreateDto bookDto;
    private static User user;

    @BeforeAll
    static void init() {
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
        bookAnother = Book.builder()
                .id(2L)
                .name("The Martian Chronicles")
                .genreBook("FICTION")
                .amount(10)
                .price(50)
                .year(1950)
                .author(author)
                .build();
        bookDto = BookRequestCreateDto.builder()
                .name("The Martian Chronicles")
                .genreBook("FICTION")
                .amount(10)
                .price(50)
                .year(1950)
                .author(author)
                .build();
        user = User.builder()
                .id(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .books(new ArrayList<>(Arrays.asList(book)))
                .state(StateItem.CREATE)
                .price(50.0)
                .user(user)
                .build();

        book.setItems(new ArrayList<>(Arrays.asList(item)));

        page = new PageImpl<>(new ArrayList<>(Arrays.asList(book)));

        pageRequest = PageRequest.of(1, 1);
    }

    @Test
    void deleteByIdTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Book actual = bookService.deleteById(1L);
        assertEquals(book, actual);
    }

    @Test
    void getByIdTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Book actual = bookService.getById(1L);
        assertEquals(book, actual);
    }

    @Test
    void createTest() {
        author.setBooks(new ArrayList<>());
        when(authorService.getById(1L)).thenReturn(author);
        when(bookRepository.saveAndFlush(book)).thenReturn(book);
        Book actual = bookService.create(book);
        assertEquals(book, actual);
    }

    @Test
    void createShouldBeExceptionTest() {
        author.setBooks(Arrays.asList(book));
        when(authorService.getById(1L)).thenReturn(author);
        when(bookMapper.toCreateDto(book)).thenReturn(bookDto);
        when(bookMapper.toDtoCreateList(any())).thenReturn(Arrays.asList(bookDto));
        EntityNotCreateException thrown = Assertions.assertThrows(EntityNotCreateException.class, () -> {
            bookService.create(book);
        }, "");
        assertEquals(String.format("The book name=%s by author with name=%s and surname=%s already exists.", book.getName(), author.getName(), author.getSurname()), thrown.getMessage());
    }

    @Test
    void updateTest() {
        when(bookRepository.saveAndFlush(book)).thenReturn(book);
        Book actual = bookService.update(book);
        assertEquals(book, actual);
    }

    @Test
    void updateShouldBeExceptionTest() {
        author.setBooks(Arrays.asList(book));
        when(bookRepository.existsBookByNameAndYearAndAuthor(any(), any(), any())).thenReturn(true);
        when(bookRepository.findByName(any())).thenReturn(Optional.of(book));
        when(authorService.getById(1L)).thenReturn(author);
        EntityNotCreateException thrown = Assertions.assertThrows(EntityNotCreateException.class, () -> {
            bookService.update(bookAnother);
        }, "");
        assertEquals(String.format("The book name=%s by author with name=%s and surname=%s already exists.", book.getName(), author.getName(), author.getSurname()), thrown.getMessage());
    }

    @Test
    void getAllTest() {
        List<Book> bookList = new ArrayList<>(List.of(book));
        when(bookRepository.findAll()).thenReturn(bookList);
        List<Book> actual = bookService.getAll();
        assertEquals(bookList, actual);
    }

    @Test
    void getAllShouldBeExceptionTest() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            bookService.getAll();
        }, "");
        assertEquals("There are no books to represent", thrown.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "0,false",
            "2,true"})
    void changeAmountDownwardTest(int amount, boolean result) {
        book.setAmount(amount);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        assertEquals(bookService.changeAmountDownward(1L), result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1,2",
            "2,3"})
    void changeAmountUpwardTest(int amount, int result) {
        book.setAmount(amount);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.changeAmountUpward(1L);
        assertEquals(book.getAmount(), result);
    }

    @Test
    void deleteBookFromItemTest() {
        when(itemService.getById(1L)).thenReturn(item);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Item actual = bookService.deleteBookFromItem(1L, 1L);
        assertEquals(0, actual.getPrice());
    }

    @Test
    void findBookByItemTest() {
        when(itemService.getById(1L)).thenReturn(item);
        when(bookRepository.findBookByItemsIn(any(), any())).thenReturn(page);
        Page<Book> bookByItem = bookService.findBookByItem(1L, pageRequest);
        assertEquals(bookByItem.getContent(), Arrays.asList(book));
    }

    @Test
    void getAllPaginatedTest() {
        when(itemService.getById(1L)).thenReturn(item);
        when(bookRepository.findBookByItemsIn(any(), any())).thenReturn(page);
        Page<Book> bookByItem = bookService.findBookByItem(1L, pageRequest);
        assertEquals(bookByItem.getContent(), Arrays.asList(book));
    }

    @Test
    void findBookByItemAndUserIdTest() {
        when(itemService.getById(1L)).thenReturn(item);
        when(bookRepository.findBookByItemsIn(any(), any())).thenReturn(page);
        Page<Book> bookByItem = bookService.findBookByItemAndUserId(1L, 1L,pageRequest);
        assertEquals(bookByItem.getContent(), Arrays.asList(book));
    }
}
