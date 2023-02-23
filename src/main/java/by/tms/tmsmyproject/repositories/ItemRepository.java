package by.tms.tmsmyproject.repositories;

import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.enums.StateItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepository extends AbstractRepository<Item> {

    Page<Item> findByStateAndUserId(StateItem stateItem, Long id, Pageable pageable);

    Page<Item> findByState(StateItem stateItem, Pageable pageable);

    Page<Item> findAllByUserId(Long id, Pageable pageable);

}
