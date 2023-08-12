package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemUser;
import ru.practicum.shareit.booking.model.BookingStatusRequest;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {

    @Transactional
    BookingDtoWithItemUser saveNewBooking(BookingDto bookingDto, long userId);

    BookingDtoWithItemUser getBooking(Long id, Long UserId);

    List<BookingDtoWithItemUser> getForBooker(BookingStatusRequest bookingStatusRequest, Long id, Integer start, Integer size);

    List<BookingDtoWithItemUser> getForOwner(BookingStatusRequest bookingStatusRequest, Long id, Integer start, Integer size);

    @Transactional
    BookingDtoWithItemUser setStatus(Long idBooking, Long userId, Boolean isApproved);

}
