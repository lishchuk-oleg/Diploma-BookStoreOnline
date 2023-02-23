package by.tms.tmsmyproject.entities.dto.book;

import by.tms.tmsmyproject.entities.dto.author.AuthorResponseGetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseGetDto extends BookDto {

    private Long id;
    private String name;
    private Integer year;
    private String genreBook;
    private AuthorResponseGetDto author;
    private Integer amount;
    private double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookResponseGetDto)) return false;
        BookResponseGetDto book = (BookResponseGetDto) o;
        return Objects.equals(name, book.name) && Objects.equals(year, book.year)
                && Objects.equals(this.author.getName(), book.author.getName())
                && Objects.equals(this.author.getSurname(), book.author.getSurname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year,this.author.getName(),this.author.getSurname());
    }

}
