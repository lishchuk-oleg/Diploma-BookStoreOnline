package by.tms.tmsmyproject.repositories;

import by.tms.tmsmyproject.entities.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbstractRepository<E extends AbstractEntity> extends JpaRepository<E, Long> {
}
