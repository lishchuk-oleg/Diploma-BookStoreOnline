package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private static Page<User> page;
    private static PageRequest pageRequest;

    private static User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .login("user")
                .password("123456")
                .name("Ivan")
                .surname("Ivanov")
                .role("ROLE_USER")
                .email("email@mail.ru")
                .build();

        page = new PageImpl<>(new ArrayList<>(Arrays.asList(user)));

        pageRequest = PageRequest.of(1, 1);
    }

    @Test
    void deleteByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User actual = userService.deleteById(1L);
        assertEquals(user, actual);
    }

    @Test
    void getByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User actual = userService.getById(1L);
        assertEquals(user, actual);
    }

    @Test
    void createTest() {
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        User actual = userService.create(user);
        assertEquals(user, actual);
    }

    @Test
    void createShouldBeExceptionTest() {
        when(userRepository.existsByLogin("user")).thenReturn(true);
        EntityNotCreateException thrown = Assertions.assertThrows(EntityNotCreateException.class, () -> {
            userService.create(user);
        }, "");

        assertEquals(String.format("The user with the login=%s already exists. ", user.getLogin()), thrown.getMessage());
    }

    @Test
    void updateTest() {
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        User actual = userService.update(user);
        assertEquals(user, actual);
    }

    @Test
    void updateWithNullLoginTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        User userUpdate=user;
        userUpdate.setLogin(null);
        User actual = userService.update(user);
        assertEquals(user, actual);
    }

    @Test
    void updatePasswordNullTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        User userUpdate=user;
        userUpdate.setPassword("");
        User actual = userService.update(user);
        assertEquals(user, actual);
    }

    @Test
    void getByLoginTest() {
        when(userRepository.getByLogin("user")).thenReturn(user);
        User actual = userService.getByLogin("user");
        assertEquals(user, actual);
    }

    @Test
    void getAllTest() {
        List<User> userList=new ArrayList<>(List.of(user));
        when(userRepository.findAll()).thenReturn(userList);
        List<User> actual = userService.getAll();
        assertEquals(userList, actual);
    }

    @Test
    void getAllShouldBeExceptionTest() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userService.getAll();
        }, "");

        assertEquals("There are no users to represent", thrown.getMessage());
    }

    @Test
    void isUserLoginTest() {
        when(userRepository.existsByLogin("user")).thenReturn(true);
        Boolean actual = userService.isUserLogin("user");
        assertEquals(true, actual);
    }

    @Test
    void isUserEmailTest() {
        when(userRepository.existsByLogin("email@mail.ru")).thenReturn(true);
        Boolean actual = userService.isUserLogin("email@mail.ru");
        assertEquals(true, actual);
    }

    @Test
    void isUserWithRoleTest() {
        when(userRepository.existsByRole("ROLE_USER")).thenReturn(true);
        Boolean actual = userService.isUserWithRole("ROLE_USER");
        assertEquals(true, actual);
    }

    @Test
    void getAllPaginatedTest() {
        when(userRepository.findAll(pageRequest)).thenReturn(page);
        Page<User> bookByItem = userService.getAllPaginated(pageRequest);
        assertEquals(bookByItem.getContent(), Arrays.asList(user));
    }

}
