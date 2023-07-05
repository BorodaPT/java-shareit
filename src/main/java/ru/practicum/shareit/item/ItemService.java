package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public class ItemService  {

    @Autowired
    @Qualifier("itemRepositoryInMemory")
    private ItemRepository itemRepository;

    public Item create(Item item) {
        return itemRepository.create(item);
    }

    public Item edit(Item item, long idItem, long idUser) {
        return itemRepository.edit(item, idItem, idUser);
    }

    public Item get(Long id) {
        return itemRepository.get(id);
    }

    public List<Item> getItems(Long userId) {
        return itemRepository.getItems(userId);
    }

    public List<Item> search(String substring) {
        return itemRepository.search(substring);
    }
}
