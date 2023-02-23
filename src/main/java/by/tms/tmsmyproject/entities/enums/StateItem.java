package by.tms.tmsmyproject.entities.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StateItem {

    CREATE("CREATE"),

    CHANGING("CHANGING"),

    PROGRESS("PROGRESS"),

    EXECUTED("EXECUTED"),

    CANCELLED("CANCELLED");

    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
