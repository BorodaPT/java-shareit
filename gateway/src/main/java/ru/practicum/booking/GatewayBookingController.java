package ru.practicum.booking;


import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.BookingStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ExceptionBadRequest;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class GatewayBookingController {

    @Autowired
    private BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.saveNewBooking(bookingDto,userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setStatus(@PathVariable("bookingId") Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.setStatus(id,userId,approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("bookingId") Long id) {
        return bookingClient.getBooking(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "from", required = false) Integer start,
                                                            @RequestParam(name = "size", required = false) Integer size,
                                                            @RequestParam(name = "state", defaultValue ="ALL", required = false) String status) {
        BookingStatusRequest statusEnum = checkStatus(status);
        if (statusEnum == null) {
            throw new ExceptionBadRequest("Error message","Unknown state: " + status);
        }
        return bookingClient.getForBooker(status, userId, start, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "from", required = false) Integer start,
                                                           @RequestParam(name = "size", required = false) Integer size,
                                                           @RequestParam(name = "state", defaultValue ="ALL", required = false) String status) {
        BookingStatusRequest statusEnum = checkStatus(status);
        if (statusEnum == null) {
            throw new ExceptionBadRequest("Error message","Unknown state: " + status);
        }
        return bookingClient.getForOwner(status, userId, start, size);
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
