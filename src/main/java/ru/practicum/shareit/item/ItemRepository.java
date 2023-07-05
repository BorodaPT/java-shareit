package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item);

    Item edit(Item item, long idItem, long idUser);

    Item get(Long id);

    List<Item> getItems(Long userId);

    List<Item> search(String substring);

}
