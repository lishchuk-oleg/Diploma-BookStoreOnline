package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.repositories.AuthorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private AuthorServiceImpl authorService;

    Author author;
    Book book;

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
    }

    @Test
    void deleteByIdTest() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        Author actual = authorService.deleteById(1L);
        assertEquals(author, actual);
    }

    @Test
    void getByIdTest() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        Author actual = authorService.getById(1L);
        assertEquals(author, actual);
    }

    @Test
    void createTest() {
        when(authorRepository.saveAndFlush(author)).thenReturn(author);
        Author actual = authorService.create(author);
        assertEquals(author, actual);
    }

    @Test
    void createShouldBeExceptionTest() {
        when(authorRepository.existsByNameAndSurname(author.getName(), author.getSurname())).thenReturn(true);
        EntityNotCreateException thrown = Assertions.assertThrows(EntityNotCreateException.class, () -> {
            authorService.create(author);
        }, "");
        assertEquals(String.format("The author with name=%s and surname=%s already exists.", author.getName(), author.getSurname()), thrown.getMessage());
    }

    @Test
    void updateTest() {
        when(authorRepository.saveAndFlush(author)).thenReturn(author);
        Author actual = authorService.create(author);
        assertEquals(author, actual);
    }

    @Test
    void updateShouldBeExceptionTest() {
        when(authorRepository.existsByNameAndSurname(author.getName(), author.getSurname())).thenReturn(true);
        when(authorRepository.getIdByNameAndSurname(author.getName(), author.getSurname())).thenReturn(2L);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        EntityNotCreateException thrown = Assertions.assertThrows(EntityNotCreateException.class, () -> {
            authorService.update(author);
        }, "");
        assertEquals(String.format("The author with name=%s and surname=%s already exists.", author.getName(), author.getSurname()), thrown.getMessage());
    }

    @Test
    void getAllTest() {
        List<Author> authorList = new ArrayList<>(List.of(author));
        when(authorRepository.findAll()).thenReturn(authorList);
        List<Author> actual = authorService.getAll();
        assertEquals(authorList, actual);
    }

    @Test
    void getAllShouldBeExceptionTest() {
        when(authorRepository.findAll()).thenReturn(new ArrayList<>());
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            authorService.getAll();
        }, "");
        assertEquals("There are no authors to represent", thrown.getMessage());
    }

    @Test
    void getAllAuthorBooksById() {
        author.setBooks(Arrays.asList(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        List<Book> bookList = authorService.getAllAuthorBooksById(1L);
        assertEquals(bookList, Arrays.asList(book));
    }

    @Test
    void getAllAuthorBooksByIdShouldBeExceptionTest() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            authorService.getAllAuthorBooksById(1L);
        }, "");
        assertEquals("There are no books to represent", thrown.getMessage());
    }

    @Test
    void getBookShouldBeExceptionNumberIncorrectTest() {
        author.setBooks(Arrays.asList(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            authorService.getBook(1L, -1);
        }, "");
        assertEquals("Number of the book is incorrect", thrown.getMessage());
    }

    @Test
    void getBookShouldBeExceptionNoBooksTest() {
        author.setBooks(Arrays.asList(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            authorService.getBook(1L, 3);
        }, "");
        assertEquals("There are no books to represent", thrown.getMessage());
    }


}
