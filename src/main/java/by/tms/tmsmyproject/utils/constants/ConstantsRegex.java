package by.tms.tmsmyproject.utils.constants;

public final class ConstantsRegex {

    private ConstantsRegex() {
    }

    public static final String LOGIN = "^[a-z][a-z0-9]*?([-_][a-z0-9]+){0,2}$";

    public static final String NAME_USER = "[A-Z][a-z]+";

    public static final String PASSWORD = "[A-Za-z0-9]{5,}";

    public static final String NAME_AUTHOR = "[A-Z][a-z-?]+";

    public static final String ALPHABET="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

}
