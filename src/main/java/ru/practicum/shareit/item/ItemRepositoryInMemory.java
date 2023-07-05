package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryInMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component("itemRepositoryInMemory")
public class ItemRepositoryInMemory implements ItemRepository {

    @Autowired
    @Qualifier("userRepositoryInMemory")
    private UserRepositoryInMemory userRepository;

    private long id;

    private HashMap<Long,Item> items;

    public ItemRepositoryInMemory() {
        id = 0;
        items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        if (!userRepository.users.containsKey(item.getOwner())) {
            throw new ExceptionNotFound("createItem","инициатор не найден");
        }
        id++;
        item.setId(id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item edit(Item item, long idItem, long idUser) {
        if (items.containsKey(idItem)) {
            Item itemBase = items.get(idItem);
            if (itemBase.getOwner() != idUser) {
                throw new ExceptionNotFound("updateItem","Операция вызвана не владельцем");
            } else {
                item.setOwner(idUser);
                if (item.getIsAvailable() != null) {
                    itemBase.setIsAvailable(item.getIsAvailable());
                }
                if (item.getName() != null) {
                    itemBase.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    itemBase.setDescription(item.getDescription());
                }
                items.put(idItem, itemBase);
                return itemBase;
            }
        } else {
            throw new ExceptionNotFound("updateItem","Вещь для обновления отсутствует");
        }
    }

    @Override
    public Item get(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new ExceptionNotFound("getItem","Вещь отсутствует");
        }
    }

    @Override
    public List<Item> getItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Item> search(String substring) {
        List<Item> result = new ArrayList<>();
        if (!substring.equals("")) {
            for (Item item : items.values()) {
                if (item.getName().toUpperCase().contains(substring.toUpperCase()) || item.getDescription().toUpperCase().contains(substring.toUpperCase())) {
                    if (item.getIsAvailable()) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }
}
