package by.tms.tmsmyproject.repositories;

import by.tms.tmsmyproject.entities.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorRepository extends AbstractRepository<Author> {

    boolean existsByNameAndSurname(String name, String surname);

    Optional<Author> getByNameAndSurname(String name, String surname);

    @Query("SELECT a.id FROM Author a WHERE a.name= :name AND a.surname= :surname")
    Long getIdByNameAndSurname(@Param("name") String name, @Param("surname") String surname);

    Page<Author> getAuthorBySurnameIsStartingWith(String firstLetter, Pageable pageable);

    Page<Author> searchAuthorByNameIsLikeOrSurnameIsLike(String text1, String text2, Pageable pageable);

}
