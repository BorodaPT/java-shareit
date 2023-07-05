package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@AllArgsConstructor
@Component
public class ItemMapper {

    public ItemDto toDTO(Item item) {
        Long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean isAvailable = item.getIsAvailable();
        ItemRequest request = item.getRequest() ;
        return new ItemDto(id, name, description, isAvailable,  request);
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getIsAvailable(),  itemDto.getRequest() );
    }

}
