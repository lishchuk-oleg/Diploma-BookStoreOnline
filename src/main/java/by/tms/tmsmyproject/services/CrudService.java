package by.tms.tmsmyproject.services;

import by.tms.tmsmyproject.entities.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudService<E extends AbstractEntity> {

    E deleteById(Long id);

    E getById(Long id);

    E create(E entity);

    E update(E entity);

    List<E> getAll();

    Page<E> getAllPaginated(Pageable pageable);

}
