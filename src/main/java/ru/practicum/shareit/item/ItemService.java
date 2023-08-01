package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;


import java.util.List;

@Transactional(readOnly = true)
public interface ItemService  {

    ItemDto saveNewItem(ItemDto itemDto, long userId);

    ItemDto saveItem(ItemDto itemDto, long userId, long itemId);

    ItemWithBookingDTO getItem(long id, long userId);

    List<ItemWithBookingDTO> getItems(long userId);

    List<ItemDto> search(String substring);

    void delete(long id);

    CommentDTO createComment(CommentDTO commentDTO, Long itemId, Long userId);




}
