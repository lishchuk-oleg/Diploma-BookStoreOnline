package by.tms.tmsmyproject.repositories;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends AbstractRepository<Book> {

    Optional<Book> findByName(String name);

    boolean existsBookByNameAndYearAndAuthor(String name, Integer year, Author author);

    Page<Book> findBookByGenreBook(String genre, Pageable pageable);

    Page<Book> findBookByAuthor_NameAndAuthor_Surname(String name, String surname, Pageable pageable);

    Page<Book> findBookByItemsIn(List<Item> items, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.name LIKE :text OR b.author.name like :text OR b.author.surname like :text")
    Page<Book> searchBook(@Param("text") String text, Pageable pageable);

    Page<Book> searchBookByNameIsLike(String text, Pageable pageable);

}