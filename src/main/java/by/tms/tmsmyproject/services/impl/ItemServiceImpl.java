package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.enums.StateItem;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.exception.EntityNotUpdateException;
import by.tms.tmsmyproject.repositories.ItemRepository;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;

    @Transactional
    @Override
    public Item deleteById(Long id) {
        Item item = getById(id);
        itemRepository.deleteById(id);
        return item;
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Item with id=%s not found", id)));
    }

    @Transactional
    @Override
    public Item create(Item item) {
        List<Book> books = item.getBooks();
        if (CollectionUtils.isEmpty(books)) {
            throw new EntityNotCreateException("Oder's list is empty");
        }
        User currentUser = CurrentUserUtils.getUser();

        item.setUser(currentUser);
        item.setDateCreate(LocalDateTime.now());
        item.setState(StateItem.CREATE);
        double price=0.0;
        for (Book book : books) {
            List<Item> items = CollectionUtils.isEmpty(book.getItems()) ? new ArrayList<>() : book.getItems();
            items.add(item);
            book.setItems(items);
            price=price+book.getPrice();
        }
        item.setPrice(price);
        itemRepository.saveAndFlush(item);
        return item;
    }

    @Transactional
    @Override
    public Item update(Item newItem) {
        return itemRepository.saveAndFlush(newItem);
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public Page<Item> getAllPaginated(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    @Override
    public Page<Item> findByStateAndUserId(String stateItem, Long id, Pageable pageable) {
        StateItem state;
        try {
            stateItem = stateItem.toUpperCase();
            if (stateItem.equals("ALL")) {
                return itemRepository.findAllByUserId(id, pageable);
            }
            state = StateItem.valueOf(stateItem);
        } catch (Exception e) {
            throw new EntityNotFoundException("State item incorrect");
        }
        return itemRepository.findByStateAndUserId(state, id, pageable);
    }

    @Override
    public Page<Item> findByState(String stateItem, Pageable pageable) {
        StateItem state;
        try {
            stateItem = stateItem.toUpperCase();
            if (stateItem.equals("ALL")) {
                return itemRepository.findAll(pageable);
            }
            state = StateItem.valueOf(stateItem);
        } catch (Exception e) {
            throw new EntityNotFoundException("State item incorrect");
        }
        return itemRepository.findByState(state, pageable);
    }

    @Override
    public Page<Item> findByUserId(Long id, Pageable pageable) {
        return itemRepository.findAllByUserId(id, pageable);
    }

    @Transactional
    @Override
    public Item changeState(Long id, String state) {
        Item item = getById(id);
        StateItem newState;
        try {
            newState = StateItem.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new EntityNotUpdateException("State is wrong");
        }
        item = changeStateAndPutDate(item, newState);
        item.setState(newState);
        return itemRepository.saveAndFlush(item);
    }

    public Item changeStateAndPutDate(Item newItem, StateItem newState) {
        StateItem oldState = newItem.getState();
        if (oldState.equals(StateItem.CANCELLED) || oldState.equals(StateItem.EXECUTED)) {
            throw new EntityNotUpdateException("Order Closed. Status cannot be changed");
        }
        if (!(oldState.equals(StateItem.CHANGING) && newState.equals(StateItem.CREATE)
                || oldState.equals(StateItem.CREATE) && newState.equals(StateItem.PROGRESS)
                || oldState.equals(StateItem.CREATE) && newState.equals(StateItem.CHANGING)
                || oldState.equals(StateItem.PROGRESS) && (newState.equals(StateItem.CANCELLED) || newState.equals(StateItem.EXECUTED))
                || !oldState.equals(newState))) {
            throw new EntityNotUpdateException("Incorrect sequence of status change");
        }
        switch (newState) {
            case CREATE -> newItem.setDateCreate(LocalDateTime.now());
            case CHANGING -> newItem.setDateChange(LocalDateTime.now());
            case EXECUTED -> newItem.setDateExecute(LocalDateTime.now());
            case CANCELLED -> newItem.setDateCancelled(LocalDateTime.now());
            case PROGRESS -> newItem.setDateProgress(LocalDateTime.now());
            default -> throw new EntityNotUpdateException("Incorrect sequence of status change");
        }
        return newItem;
    }
}

