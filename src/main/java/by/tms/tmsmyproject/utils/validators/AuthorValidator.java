package by.tms.tmsmyproject.utils.validators;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.dto.author.AuthorRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.author.AuthorRequestUpdateDto;
import by.tms.tmsmyproject.entities.mapers.AuthorMapper;
import by.tms.tmsmyproject.services.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
@AllArgsConstructor
public class AuthorValidator implements Validator {

    AuthorService authorService;
    AuthorMapper authorMapper;

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        if (obj instanceof AuthorRequestCreateDto) {
            Author author = authorMapper.toEntity((AuthorRequestCreateDto) obj);
            if (author != null && authorService.isAuthor(author)) {
                errors.rejectValue("name", "", "This author already exists in the database");
                errors.rejectValue("surname", "", "This author already exists in the database");
            }
        } else if (obj instanceof AuthorRequestUpdateDto) {
            Author author = authorMapper.toEntity((AuthorRequestUpdateDto) obj);
            if (!(!authorService.isAuthor(author) || authorService.isAuthor(author)
                    && (Objects.equals(author.getId(), authorService.getIdByNameAndSurname(author))))) {
                errors.rejectValue("name", "", "This author already exists in the database");
                errors.rejectValue("surname", "", "This author already exists in the database");
            }
        }
    }
}

