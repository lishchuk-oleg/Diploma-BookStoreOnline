package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.entities.Book;
import by.tms.tmsmyproject.entities.dto.author.AuthorResponseDeleteDto;
import by.tms.tmsmyproject.entities.dto.book.BookRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.book.BookRequestUpdateDto;
import by.tms.tmsmyproject.entities.enums.GenreBook;
import by.tms.tmsmyproject.entities.mapers.AuthorMapper;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.utils.constants.ControllerUtils;
import by.tms.tmsmyproject.utils.validators.BookValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/books")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class BooksController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    private final BookService bookService;
    private final BookValidator bookValidator;
    private final BookMapper bookMapper;

    private static int currentPage = 1;
    private static String sizeSortFieldSortDirAsUri = "";
    private static String pageReturn;
    private static String searchText;

    @ModelAttribute("path")
    public String getPathToPage() {
        return "/books/page";
    }

    @ModelAttribute("uri")
    public String getUri() {
        return "/books";
    }

    @ModelAttribute("allGenres")
    public List<String> allGenres() {
        return GenreBook.GENRES;
    }

    @ModelAttribute("allAuthors")
    public List<AuthorResponseDeleteDto> allAuthors() {
        return authorMapper.toDtoList(authorService.getAll());
    }

    @GetMapping
    public String getAllBooks() {
        return "redirect:/books/page/1";
    }

    @GetMapping("/page/{pageNumber}")
    public String getAllPage(@PathVariable("pageNumber") Integer pageNumber,
                             Model model,
                             @RequestParam(name = "size", defaultValue = "25") Integer size,
                             @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentPage = pageNumber;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = bookService.getAllPaginated(pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/books/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        model.addAttribute("list", page);
        return "books/all-books-page";
    }

    @GetMapping("/new")
    public String createBookPage(Model model,
                                 @RequestParam(name = "id", required = false) Long id,
                                 @RequestParam(name = "path", required = false) String path) {
        Book book = new Book();
        if (id != null) {
            book.setAuthor(authorService.getById(id));
            pageReturn = path;
        }
        model.addAttribute("object", book);
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("object") @Valid BookRequestCreateDto bookCreateDto,
                         BindingResult bindingResult) {
        bookValidator.validate(bookCreateDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/new";
        }
        Book book = bookMapper.toEntity(bookCreateDto);
        bookService.create(book);
        if (pageReturn != null) {
            String path = pageReturn;
            pageReturn = null;
            return "redirect:" + path;
        }
        return "redirect:/books/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @GetMapping("/{id}/edit")
    public String editPage(Model model, @PathVariable("id") Long id,
                           @RequestParam(name = "path", required = false) String path,
                           HttpServletRequest request) {
        model.addAttribute("object", bookMapper.toDtoUpdate(bookService.getById(id)));
        if (path != null) {
            pageReturn = path;
        }
        String referer = request.getHeader("Referer");
        if(referer!=null && referer.contains("search")){
            pageReturn=referer;
        }
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("object") @Valid BookRequestUpdateDto bookUpdateDto, BindingResult bindingResult) {
        bookValidator.validate(bookUpdateDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        Book book = bookMapper.toEntity(bookUpdateDto);
        bookService.update(book);
        if (pageReturn != null) {
            String path = pageReturn;
            pageReturn = null;
            return "redirect:" + path;
        }
        return "redirect:/books/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @GetMapping("/search/page/{pageNumber}")
    public String searchBook(@RequestParam(name = "text", required = false) String text,
                             @PathVariable("pageNumber") Integer pageNumber,
                             Model model,
                             @RequestParam(name = "size", defaultValue = "25") Integer size,
                             @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentPage = pageNumber;
        searchText = text == null ? searchText : text;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = bookService.searchBookByNameLikeText(searchText, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/books/search/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/books/search/page";
        model.addAttribute("search", "The result of your search: '" + searchText + "'");
        model.addAttribute("path", path);
        model.addAttribute("genre", "all");
        model.addAttribute("list", page);
        return "books/all-books-page";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        bookService.deleteById(id);
        return "redirect:/books/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }
}
