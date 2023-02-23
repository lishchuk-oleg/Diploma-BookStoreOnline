package by.tms.tmsmyproject.controllers;

import by.tms.tmsmyproject.classes.Password;
import by.tms.tmsmyproject.entities.Item;
import by.tms.tmsmyproject.entities.dto.book.BookResponseGetDto;
import by.tms.tmsmyproject.entities.dto.item.ItemResponseGetDto;
import by.tms.tmsmyproject.entities.dto.user.UserRequestUpdateClientDto;
import by.tms.tmsmyproject.entities.enums.StateItem;
import by.tms.tmsmyproject.entities.mapers.BookMapper;
import by.tms.tmsmyproject.entities.mapers.ItemMapper;
import by.tms.tmsmyproject.entities.mapers.UserMapper;
import by.tms.tmsmyproject.services.BookService;
import by.tms.tmsmyproject.services.ItemService;
import by.tms.tmsmyproject.services.UserService;
import by.tms.tmsmyproject.utils.CartUtils;
import by.tms.tmsmyproject.utils.constants.ControllerUtils;
import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import by.tms.tmsmyproject.utils.validators.PasswordChangeValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/client")
@SessionAttributes(names = {"cart", "itemsId"})
@PreAuthorize("hasRole('ROLE_USER')")
@Slf4j
public class ClientController {

    private final UserService userService;
    private final UserMapper userMapper;

    private final BookService bookService;
    private final BookMapper bookMapper;

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    private final PasswordChangeValidator passwordChangeValidator;

    private final PasswordEncoder passwordEncoder;

    private static String sizeSortFieldSortDirAsUri = "";
    private static String currentState;
    private static int currentPage = 1;

    @ModelAttribute("uri")
    public String getUri() {
        return "/client";
    }

    @ModelAttribute("path")
    public String getPathToPage() {
        return "/client/item/page";
    }

    @ModelAttribute("cart")
    public List<BookResponseGetDto> createCart() {
        return new ArrayList<>();
    }

    @ModelAttribute("itemsId")
    public List<Long> createListIdItemsInCart() {
        return new ArrayList<>();
    }

    @GetMapping
    public String clientPage(Model model) {
        model.addAttribute("user", userMapper.toDto(CurrentUserUtils.getUser()));
        return "client/client-page";
    }

    @GetMapping("/logout")
    public String logoutClient(@ModelAttribute("cart") List<BookResponseGetDto> cart,
                               @ModelAttribute("itemsId") List<Long> itemsId) {
        clearUserCart(cart, itemsId);
        log.debug("user:{} logout", CurrentUserUtils.getLogin());
        return "redirect:/logout";
    }

    @GetMapping("/edit/data")
    public String changeClientDataPage(Model model) {
        model.addAttribute("object", userMapper.toClientUpdateDto(CurrentUserUtils.getUser()));
        return "client/edit-data";
    }

    @PatchMapping("/edit/data")
    public String changeClientData(@ModelAttribute("object") @Valid UserRequestUpdateClientDto userDto,
                                   BindingResult bindingResult, @RequestParam("password") String password,
                                   Errors errors, Model model) {
        if (bindingResult.hasErrors()) {
            return "client/edit-data";
        }
        if (!passwordEncoder.matches(password, CurrentUserUtils.getUser().getPassword())) {
            errors.rejectValue("password", "", "The password entered does not match the user");
            return "client/edit-data";
        }
        userDto.setId(CurrentUserUtils.getId());
        userService.update(userMapper.toEntity(userDto));
        model.addAttribute("message", "The data has been successfully changed");
        return "client/edit-data";
    }

    @GetMapping("/edit/password")
    public String changeClientPasswordPage(@ModelAttribute("object") Password password) {
        return "client/edit-password";
    }

    @PatchMapping("/edit/password")
    public String changeClientPassword(@ModelAttribute("object") @Valid Password password, BindingResult bindingResult,
                                       Model model) {
        passwordChangeValidator.validate(password, bindingResult);

        if (!bindingResult.hasErrors()) {
            model.addAttribute("message", "The password has been successfully changed");
            userService.changePassword(password.getNewPassword());
        }
        return "client/edit-password";
    }

    @GetMapping("/books/{id}/cart")
    public String addBookToCart(@PathVariable("id") Long id,
                                @ModelAttribute("cart") List<BookResponseGetDto> cart,
                                @ModelAttribute("itemsId") List<Long> itemsId,
                                HttpServletRequest request) {
        if (bookService.changeAmountDownward(id)) {
            cart.add(bookMapper.toDtoCreate(bookService.getById(id)));
            log.debug("user:{} add book with id:{} into cart", CurrentUserUtils.getLogin(), id);
            itemsId.add(id);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/cart")
    public String getCart(@ModelAttribute("cart") List<BookResponseGetDto> cart,
                          Model model) {
        model.addAttribute("list", cart);
        model.addAttribute("sum", CartUtils.totalAmount(cart));
        return "client/cart";
    }

    @DeleteMapping("/cart/{id}")
    public String deleteBookFromCart(@PathVariable("id") Long id,
                                     @ModelAttribute("cart") List<BookResponseGetDto> cart,
                                     @ModelAttribute("itemsId") List<Long> itemsId,
                                     Model model,
                                     HttpServletRequest request) {
        if (!CollectionUtils.isEmpty(cart)) {
            cart.remove(itemsId.indexOf(id));
            itemsId.remove(id);
        }
        bookService.changeAmountUpward(id);
        model.addAttribute("list", cart);
        String referer = request.getHeader("Referer");
        log.debug("user:{} delete book with id:{} into cart", CurrentUserUtils.getLogin(), id);
        return "redirect:" + referer;
    }

    @GetMapping("/cart/item")
    public String formItemFromCart(@ModelAttribute("cart") List<BookResponseGetDto> cart,
                                   @ModelAttribute("itemsId") List<Long> itemsId) {
        Item item = new Item();
        item.setBooks(bookMapper.toEntityList(cart));
        itemService.create(item);
        cart.clear();
        itemsId.clear();
        return "redirect:/client/item/page/1?state=CREATE";
    }

    @GetMapping("/cart/clear")
    public String clearCart(@ModelAttribute("cart") List<BookResponseGetDto> cart,
                            @ModelAttribute("itemsId") List<Long> itemsId) {
        clearUserCart(cart, itemsId);
        log.debug("user:{} clear cart", CurrentUserUtils.getLogin());
        return "redirect:/client";
    }

    @GetMapping("/item/page/{pageNumber}")
    public String getItemByState(@PathVariable("pageNumber") Integer pageNumber,
                                 Model model,
                                 @RequestParam(name = "state", defaultValue = "null") String state,
                                 @RequestParam(name = "size", defaultValue = "5") Integer size,
                                 @RequestParam(name = "sortField", defaultValue = "dateCreate") String sortField,
                                 @RequestParam(name = "sortDir", defaultValue = "DESC") String sortDir) {
        currentState = state.equals("null") ? currentState : state;
        currentPage = pageNumber;
        Long currentUserId = CurrentUserUtils.getId();
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<ItemResponseGetDto> page = itemService.findByStateAndUserId(currentState, currentUserId, pageRequest).map(itemMapper::toGetDto);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/client/item/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        model.addAttribute("list", page);
        return "client/items-by-state";
    }

    @GetMapping("/item/{id}/books/page/{pageNumber}")
    public String getBooksFromItem(@PathVariable("pageNumber") Integer pageNumber,
                                   @PathVariable("id") Long id,
                                   Model model,
                                   @RequestParam(name = "size", defaultValue = "5") Integer size,
                                   @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                                   @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir) {

        currentPage = pageNumber;
        Long currentUserId = CurrentUserUtils.getId();
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, size, Sort.Direction.valueOf(sortDir), sortField);
        sizeSortFieldSortDirAsUri = ControllerUtils.getSizeSortFieldSortDirAsUri(model, pageNumber, sortField, sortDir, size);
        Page<BookResponseGetDto> page = bookService.findBookByItemAndUserId(id, currentUserId, pageRequest).map(bookMapper::toDtoCreate);
        if (pageNumber > 1 && (page.getTotalPages() < pageNumber)) {
            return "redirect:/client/item/" + id + "/books/page/" + (pageNumber - 1) + sizeSortFieldSortDirAsUri;
        }
        String path = "/client/item/" + id + "/books/page";
        model.addAttribute("item", itemMapper.toGetDto(itemService.getById(id)));
        model.addAttribute("path", path);
        model.addAttribute("list", page);
        return "client/books-in-this-item";
    }

    @GetMapping("/item/{id}")
    public String changeState(@PathVariable("id") Long id,
                              @RequestParam(name = "state") String state,
                              HttpServletRequest request) {
        String newState = state.toUpperCase();
        String oldState = itemService.getById(id).getState().toString();
        if (oldState.equals(StateItem.CREATE.toString()) && newState.equals(StateItem.CHANGING.toString())
                || newState.equals(StateItem.CREATE.toString()) && oldState.equals(StateItem.CHANGING.toString())) {
            itemService.changeState(id, state);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @DeleteMapping("/item/{id}/books/{id_book}")
    public String deleteBookFromItem(@PathVariable("id_book") Long idBook,
                                     @PathVariable("id") Long idItem,
                                     HttpServletRequest request) {
        bookService.deleteBookFromItem(idItem, idBook);
        bookService.changeAmountUpward(idBook);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    private void clearUserCart(List<BookResponseGetDto> cart, List<Long> itemsId) {
        if (!CollectionUtils.isEmpty(cart)) {
            cart.clear();
            itemsId.forEach(bookService::changeAmountUpward);
            itemsId.clear();
        }
    }
}



