package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.repositories.AuthorRepository;
import by.tms.tmsmyproject.services.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    AuthorRepository authorRepository;

    @Transactional
    @Override
    public Author deleteById(Long id) {
        Author author = getById(id);
        authorRepository.deleteById(id);
        return author;
    }

    @Override
    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Author with id=%s not found", id)));
    }

    @Transactional
    @Override
    public Author create(Author author) {
        if (isAuthor(author)) {
            throw new EntityNotCreateException(String.format(
                    "The author with name=%s and surname=%s already exists.", author.getName(), author.getSurname()));
        }
        if (author.getBooks() != null) {
            List<Book> books = author.getBooks().stream().distinct().toList();
            for (Book book : books) {
                book.setAuthor(author);
            }
        }
        return authorRepository.saveAndFlush(author);
    }

    @Transactional
    @Override
    public Author update(Author author) {
        authorRepository.findById(author.getId());
        if (!isAuthor(author) || isAuthor(author)
                && (Objects.equals(author.getId(), getIdByNameAndSurname(author)))) {
            authorRepository.saveAndFlush(author);
        } else {
            throw new EntityNotCreateException(String.format(
                    "The author with name=%s and surname=%s already exists.", author.getName(), author.getSurname()));
        }
        return author;
    }

    @Transactional
    @Override
    public List<Author> getAll() {
        List<Author> authorList = authorRepository.findAll();
        if (CollectionUtils.isEmpty(authorList)) {
            throw new EntityNotFoundException("There are no authors to represent");
        }
        return authorList;
    }

    @Override
    public boolean isAuthor(Author author) {
        return authorRepository.existsByNameAndSurname(author.getName(), author.getSurname());
    }

    @Transactional
    @Override
    public List<Book> getAllAuthorBooksById(Long id) {
        List<Book> books = getById(id).getBooks();
        if (CollectionUtils.isEmpty(books)) {
            throw new EntityNotFoundException("There are no books to represent");
        }
        return books;
    }

    @Override
    public Page<Author> getAllPaginated(Pageable pageable) {
        Page<Author> page = null;
        try {
            page = authorRepository.findAll(pageable);
        } catch (Exception e) {
            throw new EntityNotFoundException("Fieldname incorrect");
        }
        return page;
    }

    @Override
    public Author getByNameAndSurname(Author author) {
        return authorRepository.getByNameAndSurname(author.getName(), author.getSurname())
                .orElseThrow(() -> new EntityNotFoundException(String.format("The author with name=%s and surname=%s not found.", author.getName(), author.getSurname())));
    }

    @Override
    public Long getIdByNameAndSurname(Author author) {
        return authorRepository.getIdByNameAndSurname(author.getName(), author.getSurname());
    }

    @Override
    public Page<Author> findAuthorByFirstLetterSurnameOrAll(String firstLetter, Pageable pageable) {

        Page<Author> page = null;
        if (firstLetter.equals("all")) {
            page = authorRepository.findAll(pageable);
        } else {
            try {
                page = authorRepository.getAuthorBySurnameIsStartingWith(firstLetter, pageable);
            } catch (Exception e) {
                throw new EntityNotFoundException("The search data is incorrect");
            }
        }
        return page;
    }

    @Override
    public Page<Author> searchLikeText(String text, Pageable pageable) {
        text="%"+text+"%";
        return authorRepository.searchAuthorByNameIsLikeOrSurnameIsLike(text,text,pageable);
    }

    @Transactional
    @Override
    public Book getBook(Long id, Integer number) {

        List<Book> books = getAllAuthorBooksById(id);
        if (number <= 0) {
            throw new EntityNotFoundException("Number of the book is incorrect");
        }
        if (books.size() < number) {
            throw new EntityNotFoundException("There are no books to represent");
        }
        return books.get(number - 1);
    }
}
