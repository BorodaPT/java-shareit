package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;


import java.util.List;

@Transactional(readOnly = true)
public interface ItemService  {

    @Transactional
    ItemDto saveNewItem(ItemDto itemDto, long userId);

    @Transactional
    ItemDto saveItem(ItemDto itemDto, long userId, long itemId);

    ItemWithBookingDTO getItem(long id, long userId);

    List<ItemWithBookingDTO> getItems(long userId);

    List<ItemDto> search(String substring);

    @Transactional
    void delete(long id);

    @Transactional
    CommentDTO createComment(CommentDTO commentDTO, Long itemId, Long userId);

    Long countByOwner_id(Long id);



}
