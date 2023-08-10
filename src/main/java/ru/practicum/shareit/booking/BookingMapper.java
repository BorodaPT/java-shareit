package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemUser;
import ru.practicum.shareit.booking.dto.BookingForItemDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDto.UserInfo;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toDTO(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
    }

    public static List<BookingDto> toDTO(Iterable<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(toDTO(booking));
        }
        return result;
    }

    //WithItemUser
    public static BookingDtoWithItemUser toDTOWithItemUser(Booking booking) {
        ItemInfo itemInfo = new ItemInfo(booking.getItem().getId(),booking.getItem().getName());
        UserInfo userInfo = new UserInfo(booking.getBooker().getId());
        return new BookingDtoWithItemUser(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemInfo,
                userInfo,
                booking.getStatus()
        );
    }

    public static List<BookingDtoWithItemUser> toDTOWithItemUser(List<Booking> bookings) {
        List<BookingDtoWithItemUser> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(toDTOWithItemUser(booking));
        }
        return result;
    }

    public static BookingForItemDTO toBookingForItemDTO(Booking booking) {
        if (booking == null) return null;
        return new BookingForItemDTO(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
    
}
