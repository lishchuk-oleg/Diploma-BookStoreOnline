package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.User;
import by.tms.tmsmyproject.entities.dto.author.AuthorResponseGetDto;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.dto.user.UserRegistrationDto;
import by.tms.tmsmyproject.entities.enums.GenreBook;
import by.tms.tmsmyproject.entities.enums.RoleUser;
import by.tms.tmsmyproject.entities.mapers.AuthorMapper;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.constants.ConstantsRegex;
import by.tms.tmsmyproject.utils.constants.ControllerUtils;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import by.tms.tmsmyproject.utils.validators.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
@SessionAttributes(names = {"cart", "itemsId"})
@RequestMapping("/library")
@Slf4j
public class LibraryController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    private final BookService bookService;
    private final BookMapper bookMapper;

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    private static String sizeSortFieldSortDirAsUri = "";
    private static String currentGenre;
    private static String currentLetter;
    private static String searchText;
    private static String pageForReturn;

    @ModelAttribute("uri")
    public String getUri() {
        return "/library";
    }

    @ModelAttribute("uri_client")
    public String getClientUri() {
        return "/client";
    }

    @ModelAttribute("path")
    public String getPathToPage() {
        return "/library/books/page";
    }

    @ModelAttribute("alphabet")
    public String[] getAlphabet() {
        return ConstantsRegex.ALPHABET.split("");
    }

    @GetMapping
    public String libraryPage(Model model) {
        model.addAttribute("genres", GenreBook.GENRES_LIBRARY);
        return "library/library";
    }

    @GetMapping("/login")
    public String login() {
        log.debug("user:{} login", CurrentUserUtils.getLogin());
        return "redirect:/library";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("object") UserRegistrationDto userDto) {
        return "library/registration";
    }

    @PostMapping("/registration")
    public String create(@ModelAttribute("object") @Valid UserRegistrationDto userDto, BindingResult bindingResult,
                         Model model) {
        User user = userMapper.toEntity(userDto);
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "library/registration";
        }
        user.setRole(RoleUser.ROLE_USER.name());
        userService.create(user);
        model.addAttribute("registration", "success");
        return "index";
    }

    @GetMapping("/search/page/{pageNumber}")
    public String searchOnPage(@RequestParam(name = "text", required = false) String text,
                               @PathVariable(name = "pageNumber") Integer pageNumber,
                               @RequestParam(name = "size", defaultValue = "25") Integer size,
                               @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                               @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir,
                               Model model) {
        if (sortField.equals("author_name")) {
            sortField = "author.name";
        } else if (sortField.equals("author_surname")) {
            sortField = "author.surname";
        }
        searchText = text == null ? searchText : text;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<BookResponseGetDto> page = bookService.searchBookByAuthorOrNameLikeText(searchText, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/library/search/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/library/search/page";
        model.addAttribute("search", "The result of your search: '" + searchText + "'");
        model.addAttribute("path", path);
        model.addAttribute("genre", "all");
        model.addAttribute("list", page);
        return "library/books-search-page";
    }

    @GetMapping("/books/page/{pageNumber}")
    public String getBooksByGenre(@PathVariable("pageNumber") Integer pageNumber,
                                  Model model,
                                  @RequestParam(name = "genre", defaultValue = "null") String genre,
                                  @RequestParam(name = "size", defaultValue = "25") Integer size,
                                  @RequestParam(name = "sortField", defaultValue = "author_surname") String sortField,
                                  @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentGenre = genre.equals("null") ? currentGenre : genre;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<BookResponseGetDto> page = bookService.findBookByGenreOrAll(currentGenre, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/library/books/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        model.addAttribute("genre", currentGenre);
        model.addAttribute("list", page);
        return "library/books-by-genre-page";
    }

    @GetMapping("/authors/page/{pageNumber}")
    public String getAuthorsByFirstLetterSurname(@PathVariable("pageNumber") Integer pageNumber,
                                                 Model model,
                                                 @RequestParam(name = "letter", defaultValue = "null") String letter,
                                                 @RequestParam(name = "size", defaultValue = "25") Integer size,
                                                 @RequestParam(name = "sortField", defaultValue = "surname") String sortField,
                                                 @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentLetter = letter.equals("null") ? currentLetter : letter;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<AuthorResponseGetDto> page = authorService.findAuthorByFirstLetterSurnameOrAll(currentLetter, pageRequest).map(authorMapper::toDtoGet);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/library/authors/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/library/authors/page";
        model.addAttribute("letter", currentLetter);
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        return "library/authors-by-letter-page";
    }

    @GetMapping("/authors/{id}/books/page/{pageNumber}")
    public String getBooksThisAuthor(Model model,
                                     @PathVariable("id") Long id,
                                     @PathVariable("pageNumber") Integer pageNumber,
                                     @RequestParam(name = "size", defaultValue = "5") Integer size,
                                     @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                                     @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir,
                                     HttpServletRequest request) {
        pageForReturn = pageForReturn == null ? request.getHeader("Referer") : pageForReturn;
        Author author = authorService.getById(id);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = bookService.findBookByAuthor(author, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/library/authors/" + id + "/books/page" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/library/authors/" + id + "/books/page";
        model.addAttribute("letter", currentLetter);
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        model.addAttribute("author", authorMapper.toDtoUpdate(author));
        return "library/books-this-author";
    }

    @GetMapping("/authors/return")
    public String returnToAuthorsByFirstLetter() {
        String referer = pageForReturn;
        pageForReturn = null;
        return "redirect:" + referer;
    }
}

