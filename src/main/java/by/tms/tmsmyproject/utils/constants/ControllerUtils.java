package by.tms.tmsmyproject.utils.constants;

import org.springframework.ui.Model;

public final class ControllerUtils {

    private ControllerUtils() {
    }

    public static String getSizeSortFieldSortDirAsUri(Model model, Integer id, String sortField, String sortDir, Integer size) {

        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("ASC") ? "DESC" : "ASC");
        model.addAttribute("currentPage", id);

        return String.format("?sortField=%s&sortDir=%s&size=%s", sortField, sortDir, size);
    }


}
