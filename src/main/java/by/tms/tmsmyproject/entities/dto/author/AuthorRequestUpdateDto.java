package by.tms.tmsmyproject.entities.dto.author;

import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequestUpdateDto {

    private Long id;
    @Pattern(regexp = ConstantsRegex.NAME_AUTHOR, message = "Name incorrect")
    private String name;
    @Pattern(regexp = ConstantsRegex.NAME_AUTHOR, message = "Surname incorrect")
    private String surname;
    @Min(-2000)
    @Max(2022)
    private Integer birthYear;
    @Min(value = -2000, message = "Date incorrect")
    @Max(value = 2022, message = "Date incorrect")
    private Integer deathYear;

}
