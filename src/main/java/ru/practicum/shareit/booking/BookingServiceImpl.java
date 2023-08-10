package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusRequest;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Transactional
    @Override
    public BookingDtoWithItemUser saveNewBooking(BookingDto bookingDto, long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));

        ItemWithBookingDTO item = itemService.getItem(bookingDto.getItem_id(), userId);

        if (item.getOwner() == userId) {
            throw new ExceptionNotFound("Бронирование","User is owner");
        }
        if (!item.getIsAvailable()) {
            throw new ExceptionBadRequest("Бронирование","Item not Available");
        }
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
        Booking booking = BookingMapper.toBooking(bookingDto,user,ItemMapper.toItem(item,UserMapper.toUser(userService.getUser(item.getOwner()))));
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDTOWithItemUser(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithItemUser getBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findByIdAndOwnerIdORBookerId(id,userId).orElseThrow(() -> new ExceptionNotFound("selectBooking","Бронь не найдена"));
        return BookingMapper.toDTOWithItemUser(booking);
    }

    @Override
    public List<BookingDtoWithItemUser> getForBooker(BookingStatusRequest bookingStatusRequest, Long id, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            }
        }
        User user = UserMapper.toUser(userService.getUser(id));
        if (start == null && size == null) {
            switch (bookingStatusRequest) {
                case CURRENT:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idCurrent(id));
                case PAST:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idPast(id));
                case FUTURE:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idFuture(id));
                case WAITING:
                case REJECTED:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idAndStatus(id, bookingStatusRequest.name()));
                default:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_id(id));
            }
        } else {
            switch (bookingStatusRequest) {
                case CURRENT:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idCurrent(id, PageRequest.of((start/size), size, Sort.by("start_date").descending())).getContent());
                case PAST:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idPast(id, PageRequest.of((start/size), size, Sort.by("start_date").descending())).getContent());
                case FUTURE:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idFuture(id, PageRequest.of((start/size), size, Sort.by("start_date").descending())).getContent());
                case WAITING:
                case REJECTED:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idAndStatus(id, bookingStatusRequest.name(), PageRequest.of((start/size), size, Sort.by("start_date").descending())).getContent());
                default:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_id(id, PageRequest.of((start/size), size, Sort.by("start_date").descending())).getContent());
            }
        }
    }

    @Override
    public List<BookingDtoWithItemUser> getForOwner(BookingStatusRequest bookingStatusRequest, Long id, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            }
        }
        User user = UserMapper.toUser(userService.getUser(id));

        if (itemService.countByOwner_id(id) == 0) {
            throw new ExceptionNotFound("Получение списка броней для автора","Не найдены предметы для бронирования");
        }
        if (start == null && size == null) {
            switch (bookingStatusRequest) {
                case CURRENT:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idCurrent(id));
                case PAST:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idPast(id));
                case FUTURE:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idFuture(id));
                case WAITING:
                case REJECTED:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idAndStatus(id, bookingStatusRequest.name()));
                default:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_id(id));
            }
        } else {
            switch (bookingStatusRequest) {
                case CURRENT:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idCurrent(id, PageRequest.of((start/size), size)).getContent());
                case PAST:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idPast(id, PageRequest.of((start/size), size)).getContent());
                case FUTURE:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idFuture(id, PageRequest.of((start/size), size)).getContent());
                case WAITING:
                case REJECTED:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idAndStatus(id, bookingStatusRequest.name(), PageRequest.of((start/size), size)).getContent());
                default:
                    return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_id(id, PageRequest.of((start/size), size)).getContent());
            }
        }
    }

    @Transactional
    @Override
    public BookingDtoWithItemUser setStatus(Long idBooking, Long userId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(idBooking).orElseThrow(() -> new ExceptionNotFound("selectBooking","Бронь не найдена"));
        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new ExceptionNotFound("Подтверждение бронирования", "не владелец");
        }
        if (booking.getStatus() == (isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED)) {
            throw new ExceptionBadRequest("Подтверждение бронирования", "статус совпадает с установленным");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDTOWithItemUser(bookingRepository.saveAndFlush(booking));
    }
}
