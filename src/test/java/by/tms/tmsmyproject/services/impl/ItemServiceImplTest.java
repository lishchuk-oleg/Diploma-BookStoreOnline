package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.enums.StateItem;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.exception.EntityNotUpdateException;
import by.tms.tmsmyproject.repositories.ItemRepository;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private Author author;
    private Item item;
    private Item itemChangeState;
    private Book book;
    private Page<Item> pageAll;
    private PageRequest pageRequest;
    private User user;

    @BeforeEach
    void init() {
        author = Author.builder()
                .id(1L)
                .name("Ray")
                .surname("Bradbury")
                .birthYear(1920)
                .deathYear(2012)
                .build();
        book = Book.builder()
                .id(1L)
                .name("The Martian Chronicles")
                .genreBook("FICTION")
                .amount(10)
                .price(50)
                .year(1950)
                .author(author)
                .build();
        user = User.builder()
                .id(1L)
                .build();
        item = Item.builder()
                .id(1L)
                .books(new ArrayList<>(Arrays.asList(book)))
                .state(StateItem.CREATE)
                .price(50.0)
                .user(user)
                .build();

        itemChangeState = Item.builder()
                .id(1L)
                .books(new ArrayList<>(Arrays.asList(book)))
                .state(StateItem.PROGRESS)
                .price(50.0)
                .user(user)
                .build();

        book.setItems(new ArrayList<>(Arrays.asList(item)));

        pageAll = new PageImpl<>(new ArrayList<>(Arrays.asList(item, itemChangeState)));

        pageRequest = PageRequest.of(1, 1);
    }

    @Test
    void deleteByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Item actual = itemService.deleteById(1L);
        assertEquals(item, actual);
    }

    @Test
    void getByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Item actual = itemService.getById(1L);
        assertEquals(item, actual);
    }

    @Test
    void createTest() {
        try (MockedStatic<CurrentUserUtils> mockedStatic = mockStatic(CurrentUserUtils.class)) {
            mockedStatic.when(CurrentUserUtils::getUser).thenReturn(user);
            Item actual = itemService.create(item);
            assertEquals(actual.getUser().getId(), user.getId());
        }
    }

    @Test
    void createShouldBeExceptionTest() {
        item.setBooks(new ArrayList<>());
        EntityNotCreateException thrown = Assertions.assertThrows(EntityNotCreateException.class, () -> {
            itemService.create(item);
        }, "");
        assertEquals("Oder's list is empty", thrown.getMessage());
    }

    @Test
    void changeStateTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(any())).thenReturn(itemChangeState);
        Item itemChange = itemService.changeState(1L, "PROGRESS");
        assertEquals(itemChange.getState(), StateItem.PROGRESS);
    }

    @Test
    void changeStateShouldBeExceptionStateWrongTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        EntityNotUpdateException thrown = Assertions.assertThrows(EntityNotUpdateException.class, () -> {
            itemService.changeState(1L, "INCORRECT");
        }, "");
        assertEquals("State is wrong", thrown.getMessage());
    }

    @Test
    void changeStateShouldBeExceptionIncorrectSequenceTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        EntityNotUpdateException thrown = Assertions.assertThrows(EntityNotUpdateException.class, () -> {
            itemService.changeState(1L, "CREATE");
        }, "");
        assertEquals("Incorrect sequence of status change", thrown.getMessage());
    }

    @Test
    void getAllPaginatedTest() {
        when(itemRepository.findAll(pageRequest)).thenReturn(pageAll);
        Page<Item> page = itemService.getAllPaginated(pageRequest);
        assertEquals(pageAll.getContent(), page.getContent());
    }

    @Test
    void findByStateAndUserIdTest() {
        when(itemRepository.findAllByUserId(1L, pageRequest)).thenReturn(pageAll);
        Page<Item> page = itemService.findByStateAndUserId("ALL", 1L, pageRequest);
        assertEquals(pageAll.getContent(), page.getContent());
    }

    @Test
    void findByStateAndUserIdShouldBeExceptionTest() {
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            itemService.findByStateAndUserId("INCORRECT",1L, pageRequest);
        }, "");
        assertEquals("State item incorrect", thrown.getMessage());
    }

    @Test
    void findByStateTest() {
        when(itemRepository.findAll(pageRequest)).thenReturn(pageAll);
        Page<Item> page = itemService.findByState("ALL", pageRequest);
        assertEquals(pageAll.getContent(), page.getContent());
    }


}
