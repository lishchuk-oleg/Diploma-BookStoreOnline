package by.tms.tmsmyproject.entities.dto.book;

import by.tms.tmsmyproject.entities.enums.GenreBook;
import by.tms.tmsmyproject.utils.validators.enums.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseWithoutAuthorDto extends BookDto {

    @NotBlank
    private String name;
    @Min(value = -2000, message = "Date incorrect")
    @Max(value = 2022, message = "Date incorrect")
    private Integer year;
    @ValueOfEnum(enumClass = GenreBook.class, message = "Genre of book incorrect")
    private String genreBook;
    private Integer amount;
    private double price;
}
