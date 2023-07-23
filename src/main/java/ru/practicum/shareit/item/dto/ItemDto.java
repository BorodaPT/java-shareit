package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
public class ItemDto {

    private final Long id;

    @NotEmpty
    @Size(max = 100, message = "Превышена максимальная длина описания(200)")
    private String name;

    @NotEmpty
    @Size(max = 200, message = "Превышена максимальная длина описания(200)")
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
        this.request = request;
    }
}
