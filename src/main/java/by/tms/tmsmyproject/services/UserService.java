package by.tms.tmsmyproject.services;

import by.tms.tmsmyproject.entities.User;

public interface UserService extends CrudService<User> {

    boolean isUserLogin(String login);

    boolean isUserEmail(String email);

    User getByLogin(String login);

    boolean isUserWithRole(String role);

    void changePassword(String newPassword);

}
