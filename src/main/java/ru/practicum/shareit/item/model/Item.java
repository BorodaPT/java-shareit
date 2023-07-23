package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotEmpty;

@Data
public class Item {

    private Long id;

    @NotEmpty(message = "Наименование не может быть пустым или состоять только из пробелов")
    private String name;

    private String description;

    private Boolean isAvailable;

    private Long owner;

    private ItemRequest request;

    public Item(Long id, String name, String description, Boolean isAvailable, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.request = request;
    }
}
