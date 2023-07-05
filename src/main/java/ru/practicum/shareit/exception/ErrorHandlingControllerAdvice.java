package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ExceptionDataRequest.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public DefaultMessageException onExceptionDataRequest(ExceptionDataRequest e) {
        return new DefaultMessageException(e.getNameExcept(), e.getMessage());
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
