package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemUser;
import ru.practicum.shareit.booking.model.BookingStatusRequest;
import ru.practicum.shareit.exception.ExceptionBadRequest;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingDtoWithItemUser create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.saveNewBooking(bookingDto,userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoWithItemUser setStatus(@PathVariable("bookingId") Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(name = "approved") Boolean approved) {
        return bookingService.setStatus(id,userId,approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithItemUser getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long id) {
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDtoWithItemUser> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "state", defaultValue ="ALL", required = false) String status) {
        BookingStatusRequest statusEnum = checkStatus(status);
        if (statusEnum == null) {
            throw new ExceptionBadRequest("Error message","Unknown state: " + status);
        }
        return bookingService.getForBooker(statusEnum, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoWithItemUser> getBookingForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state",
                                                defaultValue ="ALL", required = false) String status) {
        BookingStatusRequest statusEnum = checkStatus(status);
        if (statusEnum == null) {
            throw new ExceptionBadRequest("Error message","Unknown state: " + status);
        }
        return bookingService.getForOwner(statusEnum, userId);
    }

    private BookingStatusRequest checkStatus(String name) {
        BookingStatusRequest result = null;
        for (BookingStatusRequest bookingStatusRequest : BookingStatusRequest.values()) {
            if (bookingStatusRequest.name().equalsIgnoreCase(name)) {
                result = BookingStatusRequest.valueOf(name);
                break;
            }
        }
        return result;
    }
}
