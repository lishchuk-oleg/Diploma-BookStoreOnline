package by.tms.tmsmyproject.services;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService extends CrudService<Book> {

    Book findByName(String name);

    boolean isBook(Book book);

    Page<Book> findBookByGenreOrAll(String genre, Pageable pageable);

    Page<Book> findBookByAuthor(Author author, Pageable pageable);

    Page<Book> findBookByItemAndUserId(Long itemId, Long userId, Pageable pageable);

    Page<Book> findBookByItem(Long itemId, Pageable pageable);

    Item deleteBookFromItem(Long idItem, Long idBook);

    Page<Book> searchBookByAuthorOrNameLikeText(String text, Pageable pageable);

    Page<Book> searchBookByNameLikeText(String text, Pageable pageable);

    boolean changeAmountDownward(Long id);

    void changeAmountUpward(Long id);
}
