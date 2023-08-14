package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionDataRequest extends RuntimeException {

    private final String nameExcept;

    private final HttpStatus status;

    public String getNameExcept() {
        return nameExcept;
    }

    public ExceptionDataRequest(String nameExcept, String message, HttpStatus status) {
        super(message);
        this.nameExcept = nameExcept;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
