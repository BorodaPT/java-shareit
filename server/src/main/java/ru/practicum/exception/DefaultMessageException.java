package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultMessageException {

    private final String name;

    @JsonProperty(value = "error")
    private final String message;

}
