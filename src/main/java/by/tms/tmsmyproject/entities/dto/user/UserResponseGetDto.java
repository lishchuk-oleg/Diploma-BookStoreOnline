package by.tms.tmsmyproject.entities.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseGetDto extends UserDto {
    private Long id;
    private String login;
    private String role;
    private String email;
    private String name;
    private String surname;

}
