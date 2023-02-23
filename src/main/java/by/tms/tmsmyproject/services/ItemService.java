package by.tms.tmsmyproject.services;

import by.tms.tmsmyproject.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService extends CrudService<Item> {

    Page<Item> findByStateAndUserId(String stateItem, Long id, Pageable pageable);

    Page<Item> findByState(String stateItem, Pageable pageable);

    Page<Item> findByUserId(Long id, Pageable pageable);

    Item changeState(Long id, String state);

}
