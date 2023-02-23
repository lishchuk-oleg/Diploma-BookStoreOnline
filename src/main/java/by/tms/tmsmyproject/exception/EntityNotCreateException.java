package by.tms.tmsmyproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotCreateException extends RuntimeException {

    public EntityNotCreateException(String message) {
        super(message);
    }
}
