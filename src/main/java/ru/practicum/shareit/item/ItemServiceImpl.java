package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto saveNewItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionNotFound("создание item","User not found"));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toDTO(item);
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionNotFound("изменение item","User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExceptionNotFound("изменение item","Item not found"));

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        }

        item.setOwner(user);

        return ItemMapper.toDTO(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingDTO getItem(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ExceptionNotFound("selectItem","Вещь не найдена"));
        List<Comment> comments = commentRepository.findAllByIdItem(id);
        if (item.getOwner().getId() == userId) {
            Booking bookingLast = bookingRepository.findLastBookingForItem(item.getId());
            Booking bookingNext = bookingRepository.findNextBookingForItem(item.getId());
            return ItemMapper.itemWithBookingDTO(item,bookingLast,bookingNext, comments);
        } else {
            return ItemMapper.itemWithBookingDTO(item,null,null, comments);
        }

    }

    @Override
    public List<ItemWithBookingDTO> getItems(long userId) {
        List<ItemWithBookingDTO> result = new ArrayList<>();
        List<Item> items = itemRepository.findByOwner_id(userId);
        for (Item item : items) {
            Booking bookingLast = bookingRepository.findLastBookingForItem(item.getId());
            Booking bookingNext = bookingRepository.findNextBookingForItem(item.getId());
            List<Comment> comments = commentRepository.findAllByIdItem(item.getId());
            result.add(ItemMapper.itemWithBookingDTO(item,bookingLast,bookingNext,comments));
        }
        return result;
    }

    @Override
    public List<ItemDto> search(String substring) {
        List<Item> items = new ArrayList<>();
        if (!substring.equals("")) {
            items = itemRepository.findByNameContainingOrDescriptionContaining(substring);
        }
        return ItemMapper.toDTO(items);
    }

    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO, Long itemId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionNotFound("добавление комментария","User not found"));
        Booking booking = bookingRepository.findByItem_idAndBooker_idAndStart_dateBefore(itemId,userId,LocalDateTime.now()).orElseThrow(() -> new ExceptionBadRequest("comment","Не зарезервирован"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExceptionNotFound("добавление комментария","Item not found"));
        Comment comment = CommentMapper.toComment(commentDTO,item,user, LocalDateTime.now());
        return CommentMapper.toDTO(commentRepository.save(comment));
    }

}
