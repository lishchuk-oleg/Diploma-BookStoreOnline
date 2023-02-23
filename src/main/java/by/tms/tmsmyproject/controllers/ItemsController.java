package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.entities.mapers.ItemMapper;
import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.constants.ControllerUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@AllArgsConstructor
@RequestMapping("/items")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ItemsController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    private final BookService bookService;
    private final BookMapper bookMapper;

    private final UserService userService;
    private final UserMapper userMapper;

    private static String sizeSortFieldSortDirAsUri = "";
    private static String currentState;
    private static int currentPage = 1;
    private static String previousPageWhenDelete;

    @ModelAttribute("path")
    public String getPathToPage() {
        return "/items/page";
    }

    @ModelAttribute("uri")
    public String getUri() {
        return "/items";
    }

    @GetMapping("/page/{pageNumber}")
    public String getItemsByState(@PathVariable("pageNumber") Integer pageNumber,
                             Model model,
                             @RequestParam(name = "state", defaultValue = "null") String state,
                             @RequestParam(name = "size", defaultValue = "5") Integer size,
                             @RequestParam(name = "sortField", defaultValue = "dateCreate") String sortField,
                             @RequestParam(name = "sortDir", defaultValue = "DESC") String sortDir) {
        currentPage = pageNumber;
        currentState = state.equals("null") ? currentState : state;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = itemService.findByState(currentState, pageRequest).map(itemMapper::toGetDto);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/items/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        model.addAttribute("list", page);
        return "items/all-items-page";
    }

    @GetMapping("/{id}/books/page/{pageNumber}")
    public String getBooksFromItem(@PathVariable("pageNumber") Integer pageNumber,
                                   @PathVariable("id") Long id,
                                   Model model, HttpServletRequest request,
                                   @RequestParam(name = "size", defaultValue = "5") Integer size,
                                   @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                                   @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {
        currentPage = pageNumber;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<BookResponseGetDto> page = bookService.findBookByItem(id, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/items/" + id + "/books/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        previousPageWhenDelete = previousPageWhenDelete == null ? request.getHeader("Referer") : previousPageWhenDelete;
        String path = "/items/" + id + "/books/page";
        model.addAttribute("item", itemMapper.toGetDto(itemService.getById(id)));
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        return "items/books-in-this-item";
    }

    @GetMapping("/users/{id}/page/{pageNumber}")
    public String getItemFromUserById(@PathVariable("id") Long id, @PathVariable("pageNumber") Integer pageNumber,
                                      Model model,
                                      @RequestParam(name = "state", defaultValue = "null") String state,
                                      @RequestParam(name = "size", defaultValue = "5") Integer size,
                                      @RequestParam(name = "sortField", defaultValue = "dateCreate") String sortField,
                                      @RequestParam(name = "sortDir", defaultValue = "DESC") String sortDir) {
        currentPage = pageNumber;
        currentState = state.equals("null") ? currentState : state;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        var page = itemService.findByUserId(id, pageRequest).map(itemMapper::toGetDto);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/items/users/" + id + "/page" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/items/users/" + id + "/page";
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        model.addAttribute("user", userMapper.toDto(userService.getById(id)));
        return "items/items-this-user";
    }

    @GetMapping("/{id}")
    public String changeState(@PathVariable("id") Long id,
                              @RequestParam(name = "state") String state,
                              HttpServletRequest request) {

        itemService.changeState(id, state);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping("/{id}/books/{id_book}")
    public String deleteBookFromItem(@PathVariable("id_book") Long idBook,
                                     @PathVariable("id") Long idItem,
                                     HttpServletRequest request) {
        bookService.deleteBookFromItem(idItem, idBook);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable("id") Long id) {
        itemService.deleteById(id);
        return "redirect:" + previousPageWhenDelete;
    }

}
