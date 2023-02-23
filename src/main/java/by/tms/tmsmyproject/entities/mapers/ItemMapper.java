package by.tms.tmsmyproject.entities.mapers;

import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.dto.item.ItemResponseGetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class})
public interface ItemMapper {

    ItemResponseGetDto toGetDto(Item item);
}
