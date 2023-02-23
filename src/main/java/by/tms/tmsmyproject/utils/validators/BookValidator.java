package by.tms.tmsmyproject.utils.validators;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.dto.book.BookRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.book.BookRequestUpdateDto;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class BookValidator implements Validator {

    BookService bookService;
    AuthorService authorService;
    BookMapper bookMapper;

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        if (obj instanceof BookRequestCreateDto) {
            BookRequestCreateDto book = (BookRequestCreateDto) obj;
            Author author = authorService.getByNameAndSurname(book.getAuthor());
            List<BookRequestCreateDto> books = bookMapper.toDtoCreateList(author.getBooks());
            if (books.contains(book)) {
                errors.rejectValue("name", "", "This books by this author already exists in the database");
                errors.rejectValue("author", "", "This books by this author already exists in the database");
            }
        } else if (obj instanceof BookRequestUpdateDto) {
            Book book = bookMapper.toEntity((BookRequestUpdateDto) obj);
            if (bookService.isBook(book) && !Objects.equals(book.getId(), bookService.findByName(book.getName()).getId())) {
                errors.rejectValue("name", "", "This books by this author already exists in the database");
            }
        }
    }
}
