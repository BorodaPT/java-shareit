package ru.practicum.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.dto.CommentDTO;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithBookingDTO;


import java.util.List;

@Transactional(readOnly = true)
public interface ItemService  {

    @Transactional
    ItemDto saveNewItem(ItemDto itemDto, long userId);

    @Transactional
    ItemDto saveItem(ItemDto itemDto, long userId, long itemId);

    ItemWithBookingDTO getItem(long id, long userId);

    List<ItemWithBookingDTO> getItems(long userId, Integer start, Integer size);

    List<ItemDto> search(String substring, Integer start, Integer size);

    @Transactional
    void delete(long id);

    @Transactional
    CommentDTO createComment(CommentDTO commentDTO, Long itemId, Long userId);

    Long countByOwner_id(Long id);

    List<ItemDto> getByRequestId(long idRequest);

}
