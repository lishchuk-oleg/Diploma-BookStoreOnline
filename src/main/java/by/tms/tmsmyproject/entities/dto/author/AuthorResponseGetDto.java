package by.tms.tmsmyproject.entities.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponseGetDto extends AuthorDto{

    private Long id;
    private String name;
    private String surname;
    private Integer birthYear;
    private Integer deathYear;

}
