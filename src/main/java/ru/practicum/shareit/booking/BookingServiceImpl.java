package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusRequest;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public BookingDtoWithItemUser saveNewBooking(BookingDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionNotFound("Бронирования","User not found"));
        Item item = itemRepository.findById(bookingDto.getItem_id())
                .orElseThrow(() -> new ExceptionNotFound("Бровнирования","Item not found"));
        if (item.getOwner().getId() == userId) {
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
        Booking booking = BookingMapper.toBooking(bookingDto,user,item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDTOWithItemUser(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithItemUser getBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findByIdAndOwnerIdORBookerId(id,userId).orElseThrow(() -> new ExceptionNotFound("selectBooking","Бронь не найдена"));
        return BookingMapper.toDTOWithItemUser(booking);
    }

    @Override
    public List<BookingDtoWithItemUser> getForBooker(BookingStatusRequest bookingStatusRequest, Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound("Бронирования","User not found"));

        switch (bookingStatusRequest) {
            case CURRENT:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idCurrent(id));
            case PAST:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idPast(id));
            case FUTURE:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idFuture(id));
            case WAITING:
            case REJECTED:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_idAndStatus(id,bookingStatusRequest.name()));
            default:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByBooker_id(id));
        }
    }

    @Override
    public List<BookingDtoWithItemUser> getForOwner(BookingStatusRequest bookingStatusRequest, Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound("Бронирования","User not found"));

        if (itemRepository.countByOwner_id(id) == 0) {
            throw new ExceptionNotFound("Получение списока броней для автора","Не найдены предметы для бронирования");
        }

        switch (bookingStatusRequest) {
            case CURRENT:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idCurrent(id));
            case PAST:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idPast(id));
            case FUTURE:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idFuture(id));
            case WAITING:
            case REJECTED:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_idAndStatus(id,bookingStatusRequest.name()));
            default:
                return BookingMapper.toDTOWithItemUser(bookingRepository.findByOwner_id(id));
        }
    }

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