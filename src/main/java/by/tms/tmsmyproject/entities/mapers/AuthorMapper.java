package by.tms.tmsmyproject.entities.mapers;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.dto.author.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface AuthorMapper {

    Author toEntity(AuthorRequestCreateDto authorDto);

    Author toEntity(AuthorRequestUpdateDto authorDto);

    AuthorResponseCreateDto toDtoCreate(Author author);

    AuthorRequestCreateDto toDtoRequestCreate(Author author);

    AuthorRequestUpdateDto toDtoUpdate(Author author);

    AuthorResponseDeleteDto toDtoDelete(Author author);

    AuthorResponseGetDto toDtoGet(Author author);

    List<AuthorResponseDeleteDto> toDtoList(List<Author> authors);

}
