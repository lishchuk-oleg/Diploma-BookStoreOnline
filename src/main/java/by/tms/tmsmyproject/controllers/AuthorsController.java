package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.entities.Author;
import by.tms.tmsmyproject.entities.dto.author.AuthorRequestCreateDto;
import by.tms.tmsmyproject.entities.dto.author.AuthorRequestUpdateDto;
import by.tms.tmsmyproject.entities.dto.author.AuthorResponseCreateDto;
import by.tms.tmsmyproject.entities.mapers.AuthorMapper;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.services.AuthorService;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.utils.constants.ControllerUtils;
import by.tms.tmsmyproject.utils.validators.AuthorValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/authors")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorValidator authorValidator;
    private final AuthorMapper authorMapper;

    private final BookService bookService;
    private final BookMapper bookMapper;

    private static int currentPage = 1;
    private static String sizeSortFieldSortDirAsUri = "";
    private static String pageForReturn;
    private static String searchText;

    @ModelAttribute("path")
    public String getPathToPage() {
        return "/authors/page";
    }

    @ModelAttribute("uri")
    public String getUri() {
        return "/authors";
    }

    @GetMapping
    public String getAllAuthors() {
        return "redirect:/authors/page/1";
    }

    @GetMapping("/new")
    public String newAuthor(@ModelAttribute("object") Author author) {
        return "authors/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("object") @Valid AuthorRequestCreateDto authorCreateDto,
                         BindingResult bindingResult) {
        authorValidator.validate(authorCreateDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "authors/new";
        }
        Author author = authorMapper.toEntity(authorCreateDto);
        authorService.create(author);
        return "redirect:/authors/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @GetMapping("/page/{pageNumber}")
    public String getAllPage(@PathVariable("pageNumber") Integer pageNumber,
                             Model model,
                             @RequestParam(name = "size", defaultValue = "5") Integer size,
                             @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentPage = pageNumber;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = authorService.getAllPaginated(pageRequest).map(authorMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/authors/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        model.addAttribute("list", page);
        return "authors/all-authors-page";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("object", authorMapper.toDtoUpdate(authorService.getById(id)));
        return "authors/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("object") @Valid AuthorRequestUpdateDto authorUpdateDto, BindingResult bindingResult) {
        authorValidator.validate(authorUpdateDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "authors/edit";
        }
        Author author = authorMapper.toEntity(authorUpdateDto);
        authorService.update(author);
        return "redirect:/authors/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @GetMapping("/{id}/books/page/{pageNumber}")
    public String authorBooks(Model model,
                              @PathVariable("id") Long id,
                              @PathVariable("pageNumber") Integer pageNumber,
                              @RequestParam(name = "size", defaultValue = "5") Integer size,
                              @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                              @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir,
                              HttpServletRequest request) {
        currentPage = pageNumber;
        if (pageNumber == 1) {
            pageForReturn = request.getHeader("Referer");
        }
        Author author = authorService.getById(id);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = bookService.findBookByAuthor(author, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/authors/" + id + "/books/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/authors/" + id + "/books/page";
        if(pageForReturn!=null && pageForReturn.contains("search")){
            model.addAttribute("search", searchText);
        }
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        model.addAttribute("author", authorMapper.toDtoUpdate(author));
        return "authors/author-books";
    }

    @GetMapping("/return")
    public String returnToAllAuthors() {
        return "redirect:" + pageForReturn;
    }

    @GetMapping("/{id}/books/new")
    public String addNewBookThisAuthorPage(@PathVariable("id") Long id,
                                           HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return "redirect:/books/new?path=" + referer + "&id=" + id;
    }

    @GetMapping("/{idAuthor}/books/{id}/edit")
    public String editBookThisAuthorPage(@PathVariable("id") Long id,
                                         @PathVariable("idAuthor") Long idAuthor,
                                         HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return "redirect:/books/" + id + "/edit?path=" + referer;
    }

    @GetMapping("/search/page/{pageNumber}")
    public String searchAuthor(@RequestParam(name = "text", required = false) String text,
                               @PathVariable(name = "pageNumber") Integer pageNumber,
                               @RequestParam(name = "size", defaultValue = "5") Integer size,
                               @RequestParam(name = "sortField", defaultValue = "surname") String sortField,
                               @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir,
                               Model model, HttpServletRequest request) {

        searchText = text == null ? searchText : text;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<AuthorResponseCreateDto> page = authorService.searchLikeText(searchText, pageRequest).map(authorMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/search/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/authors/search/page";
        model.addAttribute("search", "The result of your search: '" + searchText + "'");
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        return "authors/all-authors-page";
    }

    @GetMapping("/search/return")
    public String returnToSearchResult() {
        String text=searchText;
        searchText=null;
        return "redirect:/authors/search/page/1?text=" + text;
    }

    @DeleteMapping("/{id}")
    public String deleteAuthor(@PathVariable("id") Long id) {
        authorService.deleteById(id);
        return "redirect:/authors/page/" + currentPage + sizeSortFieldSortDirAsUri;
    }

    @DeleteMapping("/{idAuthor}/books/{id}")
    public String deleteBookThisAuthor(@PathVariable("id") Long id,
                                       @PathVariable("idAuthor") Long idAuthor,
                                       HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        bookService.deleteById(id);
        return "redirect:" + referer;
    }
}
