package ru.practicum.booking;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.client.BaseClient;
import ru.practicum.exception.ExceptionBadRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewBooking(BookingDto bookingDto, Long userId) {
        if (bookingDto.getStart() == null) {
            throw new ExceptionBadRequest("Бронирование","Дата начала пуста");
        }

        if (bookingDto.getEnd() == null) {
            throw new ExceptionBadRequest("Бронирование","Дата завершения пуста");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ExceptionBadRequest("Бронирование","Дата начала меньше текущей");
        }

        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ExceptionBadRequest("Бронирование","Дата завершения меньше текущей");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ExceptionBadRequest("Бронирование","Некорректные даты бролванирования");
        }

        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> setStatus(long id, long userId, Boolean approved) {
        return patch("/" + id + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(long id, long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getForBooker(String status, long userId, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            } else {
                return get("?state={state}&from={from}&size={size}", userId, Map.of(
                        "state", status,
                        "from", start,
                        "size", size));
            }
        } else {
            return get("?state={state}", userId, Map.of("state", status));
        }
    }

    public ResponseEntity<Object> getForOwner(String status, Long userId, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            } else {
                return get("/owner?state={state}&from={from}&size={size}", userId, Map.of(
                        "state", status,
                        "from", start,
                        "size", size));
            }
        } else {
            return get("/owner?state={state}", userId,  Map.of("state", status));
        }

    }
}
