package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
public class ItemDto {

    private final Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @JsonProperty(value = "available")
    @NotNull
    private Boolean isAvailable;

    private ItemRequest request;

    public ItemDto(Long id, String name, String description, Boolean isAvailable,  ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        //this.owner = owner;
        this.request = request;
    }
}
