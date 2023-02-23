package by.tms.tmsmyproject.entities.dto.book;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.enums.GenreBook;
import by.tms.tmsmyproject.utils.validators.enums.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequestCreateDto extends BookDto{

    @NotBlank
    private String name;
    @Min(value = -2000, message = "Date incorrect")
    @Max(value = 2022, message = "Date incorrect")
    private Integer year;
    @ValueOfEnum(enumClass = GenreBook.class, message = "Genre of book incorrect")
    private String genreBook;
    private Author author;

    @NotNull
    @Min(value = 0, message = "Amount is incorrect")
    private Integer amount;

    @NotNull
    @Min(value = 0, message = "Price is incorrect")
    private double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookRequestCreateDto)) return false;
        BookRequestCreateDto that = (BookRequestCreateDto) o;
        return Objects.equals(name, that.name) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year);
    }
}
