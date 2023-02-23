package by.tms.tmsmyproject.entities.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum RoleUser {

    ROLE_USER("User"),

    ROLE_ADMIN("Admin");

    private final String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public final static List<String> ROLES = Arrays.stream(RoleUser.values())
            .map(role->role.value).collect(Collectors.toList());
}
