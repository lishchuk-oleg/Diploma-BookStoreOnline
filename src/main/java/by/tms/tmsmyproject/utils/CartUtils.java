package by.tms.tmsmyproject.utils;

import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CartUtils {

    public Double totalAmount(List<BookResponseGetDto> list) {
        if (list == null || list.isEmpty()) {
            return 0.0;
        }
        return Math.round(list.stream().mapToDouble(BookResponseGetDto::getPrice).sum()*10)/10.0;
    }
}
