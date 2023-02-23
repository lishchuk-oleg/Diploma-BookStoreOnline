package by.tms.tmsmyproject.entities.dto.author;

import by.tms.tmsmyproject.entities.dto.book.BookResponseWithoutAuthorDto;
import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequestCreateDto extends AuthorDto {

    @Pattern(regexp = ConstantsRegex.NAME_AUTHOR, message = "Name incorrect")
    private String name;
    @Pattern(regexp = ConstantsRegex.NAME_AUTHOR, message = "Surname incorrect")
    private String surname;
    @Min(-2000)
    @Max(2022)
    private Integer birthYear;
    @Min(-2000)
    @Max(2022)
    private Integer deathYear;
    private List<BookResponseWithoutAuthorDto> books;
}
