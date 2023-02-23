package by.tms.tmsmyproject.services;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService extends CrudService<Author> {

    boolean isAuthor(Author author);

    List<Book> getAllAuthorBooksById(Long id);

    Book getBook(Long id, Integer number);

    Author getByNameAndSurname(Author author);

    Long getIdByNameAndSurname(Author author);

    Page<Author> findAuthorByFirstLetterSurnameOrAll(String firstLetter,Pageable pageable);

    Page<Author> searchLikeText(String text, Pageable pageable);
}
