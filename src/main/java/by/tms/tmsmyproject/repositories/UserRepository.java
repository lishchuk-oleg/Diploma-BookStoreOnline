package by.tms.tmsmyproject.repositories;

import by.tms.tmsmyproject.entities.User;

public interface UserRepository extends AbstractRepository<User> {

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    User getByLogin(String login);

    boolean existsByRole(String role);

}
