package by.tms.tmsmyproject.entities.mapers;

import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.dto.book.BookRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.book.BookRequestUpdateDto;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.dto.book.BookResponseWithoutAuthorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequestCreateDto booksRequestCreateDto);

    Book toEntity(BookRequestUpdateDto bookRequestUpdateDto);

    BookResponseWithoutAuthorDto toDto(Book book);

    BookResponseGetDto toDtoCreate(Book book);

    BookRequestUpdateDto toDtoUpdate(Book book);

    BookRequestCreateDto toCreateDto(Book book);

    List<BookResponseWithoutAuthorDto> toDtoList(List<Book> books);

    List<BookRequestCreateDto> toDtoCreateList(List<Book> books);

    List<Book> toDtoListCreate(List<BookResponseWithoutAuthorDto> booksDto);

    List<Book> toEntityList(List<BookResponseGetDto> booksDto);

}



