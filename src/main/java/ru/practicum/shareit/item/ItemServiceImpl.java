package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto saveNewItem(ItemDto itemDto, long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toDTO(item);
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId, long itemId) {
        User user = UserMapper.toUser(userService.getUser(userId));
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
    public List<ItemWithBookingDTO> getItems(long userId, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            }
        }
        List<ItemWithBookingDTO> result = new ArrayList<>();
        List<Item> items;
        if (start != null && size != null) {
            items = itemRepository.findByOwner_id(userId, PageRequest.of(start, size)).getContent();
        } else {
            items = itemRepository.findByOwner_id(userId);
        }
        for (Item item : items) {
            Booking bookingLast = bookingRepository.findLastBookingForItem(item.getId());
            Booking bookingNext = bookingRepository.findNextBookingForItem(item.getId());
            List<Comment> comments = commentRepository.findAllByIdItem(item.getId());
            result.add(ItemMapper.itemWithBookingDTO(item,bookingLast,bookingNext,comments));
        }
        return result;
    }

    @Override
    public List<ItemDto> search(String substring, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            }
        }
        List<Item> items = new ArrayList<>();
        if (!substring.equals("")) {
            if (start != null && size != null) {
                items = itemRepository.findByNameContainingOrDescriptionContaining(substring, PageRequest.of(start, size)).getContent();
            } else {
                items = itemRepository.findByNameContainingOrDescriptionContaining(substring);
            }
        }
        return ItemMapper.toDTO(items);
    }

    @Transactional
    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO, Long itemId, Long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        Booking booking = bookingRepository.findByItem_idAndBooker_idAndStart_dateBefore(itemId,userId,LocalDateTime.now()).orElseThrow(() -> new ExceptionBadRequest("comment","Не зарезервирован"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExceptionNotFound("добавление комментария","Item not found"));
        Comment comment = CommentMapper.toComment(commentDTO,item,user, LocalDateTime.now());
        return CommentMapper.toDTO(commentRepository.save(comment));
    }

    public Long countByOwner_id(Long id) {
        return itemRepository.countByOwner_id(id);
    }

    @Override
    public List<ItemDto> getByRequestId(long idRequest) {
        return ItemMapper.toDTO(itemRepository.findByRequest_id(idRequest));
    }
}
