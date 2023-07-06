package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ExceptionDataRequest.class)
    public ResponseEntity<Map<String, String>> onExceptionDataRequest(ExceptionDataRequest e) {
        return new ResponseEntity<>(Map.of(e.getNameExcept(), e.getMessage()), e.getStatus());//
    }

    //ошибка наличия
    @ExceptionHandler(ExceptionNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public DefaultMessageException onExceptionNotFound(ExceptionNotFound e) {
        return new DefaultMessageException(e.getNameExcept(), e.getMessage());
    }

    @ExceptionHandler(ExceptionBadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public DefaultMessageException onExceptionBadRequest(ExceptionBadRequest e) {
        return new DefaultMessageException(e.getNameExcept(), e.getMessage());
    }
}
