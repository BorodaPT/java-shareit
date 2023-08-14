package ru.practicum.item;

import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithBookingDTO;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toDTO(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner().getId(),
                item.getRequest_id()
        );
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getIsAvailable(),
                user,
                itemDto.getRequestId()
        );
    }

    public static Item toItem(ItemWithBookingDTO itemDto, User user) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getIsAvailable(),
                user,
                itemDto.getRequestId()
        );
    }

    public static List<ItemDto> toDTO(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(toDTO(item));
        }
        return result;
    }

    //ItemWithBookingDTO
    public static ItemWithBookingDTO itemWithBookingDTO(Item item, Booking bookingLast, Booking bookingNext, List<Comment> comments) {
        ItemWithBookingDTO itemWithBookingDTO = new ItemWithBookingDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest_id(),
                item.getOwner().getId(),
                BookingMapper.toBookingForItemDTO(bookingLast),
                BookingMapper.toBookingForItemDTO(bookingNext),
                CommentMapper.toDTO(comments)
        );
        return itemWithBookingDTO;
    }

}
