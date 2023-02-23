package by.tms.tmsmyproject.services.impl;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.dto.book.BookRequestCreateDto;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.exception.EntityNotCreateException;
import by.tms.tmsmyproject.exception.EntityNotFoundException;
import by.tms.tmsmyproject.repositories.BookRepository;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    BookRepository bookRepository;
    AuthorService authorService;
    ItemService itemService;

    BookMapper bookMapper;

    @Transactional
    @Override
    public Book deleteById(Long id) {
        Book book = getById(id);
        bookRepository.deleteById(id);
        return book;
    }

    @Transactional
    @Override
    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Book with id=%s not found", id)));
    }

    @Transactional
    @Override
    public Book create(Book book) {
        Author author = authorService.getById(book.getAuthor().getId());
        List<Book> books = author.getBooks();

        List<BookRequestCreateDto> booksDto = bookMapper.toDtoCreateList(author.getBooks());
        if (booksDto.contains(bookMapper.toCreateDto(book))) {
            throw new EntityNotCreateException(String.format(
                    "The book name=%s by author with name=%s and surname=%s already exists.", book.getName(), author.getName(), author.getSurname()));
        }
        books.add(book);
        author.setBooks(books);
        bookRepository.saveAndFlush(book);
        return book;
    }

    @Transactional
    @Override
    public Book update(Book book) {
        if (isBook(book) && !Objects.equals(book.getId(), findByName(book.getName()).getId())) {
            Author author = authorService.getById(book.getAuthor().getId());
            throw new EntityNotCreateException(String.format(
                    "The book name=%s by author with name=%s and surname=%s already exists.", book.getName(), author.getName(), author.getSurname()));
        }
        return bookRepository.saveAndFlush(book);
    }

    @Transactional
    @Override
    public List<Book> getAll() {
        List<Book> bookList = bookRepository.findAll();
        if (CollectionUtils.isEmpty(bookList)) {
            throw new EntityNotFoundException("There are no books to represent");
        }
        return bookList;
    }

    @Override
    public Page<Book> getAllPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Book findByName(String name) {
        return bookRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Book with name=%s not found", name)));
    }

    @Override
    public boolean isBook(Book book) {
        return bookRepository.existsBookByNameAndYearAndAuthor(book.getName(), book.getYear(), book.getAuthor());
    }

    @Override
    public Page<Book> findBookByGenreOrAll(String genre, Pageable pageable) {
        Page<Book> page = null;
        if (genre.equals("all")) {
            page = bookRepository.findAll(pageable);
        } else {
            try {
                page = bookRepository.findBookByGenreBook(genre, pageable);
            } catch (Exception e) {
                throw new EntityNotFoundException("The search data is incorrect");
            }
        }
        return page;
    }

    @Override
    public Page<Book> findBookByAuthor(Author author, Pageable pageable) {
        return bookRepository.findBookByAuthor_NameAndAuthor_Surname(author.getName(), author.getSurname(), pageable);
    }

    @Override
    public Page<Book> findBookByItemAndUserId(Long itemId, Long userId, Pageable pageable) {
        Item item = itemService.getById(itemId);
        if (!Objects.equals(item.getUser().getId(), userId)) {
            throw new EntityNotFoundException(String.format("There isn't item with id=%s in user with id=%s", itemId, userId));
        }
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        return bookRepository.findBookByItemsIn(itemList, pageable);
    }

    @Override
    public Page<Book> findBookByItem(Long itemId, Pageable pageable) {
        Item item = itemService.getById(itemId);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        return bookRepository.findBookByItemsIn(itemList, pageable);
    }

    @Transactional
    @Override
    public Item deleteBookFromItem(Long idItem, Long idBook) {
        Item item = itemService.getById(idItem);
        Book book = getById(idBook);
        List<Book> booksList = item.getBooks();
        booksList.remove(book);
        item.setBooks(booksList);
        if (booksList.isEmpty()) {
            itemService.changeState(idItem, "CANCELLED");
        }
        List<Item> itemsList = book.getItems();
        itemsList.remove(item);
        book.setItems(itemsList);
        item.setPrice(item.getPrice()-book.getPrice());
        bookRepository.saveAndFlush(book);
        return item;
    }

    @Override
    public Page<Book> searchBookByAuthorOrNameLikeText(String text, Pageable pageable) {
        text = "%" + text + "%";
        return bookRepository.searchBook(text, pageable);
    }

    @Override
    public Page<Book> searchBookByNameLikeText(String text, Pageable pageable) {
        text = "%" + text + "%";
        return bookRepository.searchBookByNameIsLike(text, pageable);
    }

    @Transactional
    @Override
    public boolean changeAmountDownward(Long id) {
        Book book = getById(id);
        if (book.getAmount() == 0) {
            return false;
        }
        book.setAmount(book.getAmount() - 1);
        bookRepository.saveAndFlush(book);
        return true;
    }

    @Transactional
    @Override
    public void changeAmountUpward(Long id) {
        Book book = getById(id);
        book.setAmount(book.getAmount() + 1);
        bookRepository.saveAndFlush(book);
    }
}
