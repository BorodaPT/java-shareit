package ru.practicum.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.BookingService;
import ru.practicum.booking.BookingServiceImpl;
import ru.practicum.booking.dto.BookingDtoWithItemUser;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.model.BookingStatusRequest;
import ru.practicum.item.ItemMapper;
import ru.practicum.item.ItemService;
import ru.practicum.item.model.Item;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class BookingServiceImplTest {

    private BookingService service;

    private BookingRepository bookingRepository;

    private ItemService itemService;

    private UserService userService;

    private User userOwner;

    private User userBooker;

    private Item item;

    private LocalDateTime createDateStart;

    private LocalDateTime createDateEnd;

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemService = Mockito.mock(ItemService.class);
        userService = Mockito.mock(UserService.class);
        service = new BookingServiceImpl(bookingRepository,itemService,userService);
        userOwner = new User(
                1L,
                "Пушкин",
                "push@mail.ru");
        userBooker = new User(
                2L,
                "Лермонтов",
                "lermon@mail.ru");
        item = new Item(1L,"перо","орлиное",true,userOwner,null);

        createDateStart = LocalDateTime.now().plusDays(1).withNano(0);
        createDateEnd = LocalDateTime.now().plusDays(6).withNano(0);
    }

    @Test
    void saveNewBooking() {
        Booking booking = new Booking(
                1L,
                createDateStart,
                createDateEnd,
                item,
                userBooker,
                BookingStatus.WAITING);

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(ItemMapper.itemWithBookingDTO(item,null, null, null));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDtoWithItemUser bookingDto = service.saveNewBooking(BookingMapper.toDTO(booking), 2L);

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void setStatus() {
        Booking booking = new Booking(
                1L,
                createDateStart,
                createDateEnd,
                item,
                userBooker,
                BookingStatus.WAITING);
        Booking bookingRes = new Booking(
                1L,
                createDateStart,
                createDateEnd,
                item,
                userBooker,
                BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(ItemMapper.itemWithBookingDTO(item,null, null, null));

        when(bookingRepository.saveAndFlush(any(Booking.class)))
                .thenReturn(bookingRes);

        BookingDtoWithItemUser bookingDto = service.setStatus(1L, 1L, true);
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.APPROVED));

    }

    @Test
    void getBooking() {
        Booking booking = new Booking(
                1L,
                createDateStart,
                createDateEnd,
                item,
                userBooker,
                BookingStatus.WAITING);
        when(bookingRepository.findByIdAndOwnerIdORBookerId(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoWithItemUser bookingDto = service.getBooking(1L, 1L);

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void getForBookerAll() {
        List<Booking> bookingList = List.of(
                new Booking(
                    1L,
                    createDateStart,
                    createDateEnd,
                    item,
                    userBooker,
                    BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(bookingRepository.findByBooker_id(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingDtoWithItemUser> bookingListRes = service.getForBooker(BookingStatusRequest.ALL, 2L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));
    }

    @Test
    void getForBookerCURRENT() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(bookingRepository.findByBooker_idCurrent(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingDtoWithItemUser> bookingListRes = service.getForBooker(BookingStatusRequest.CURRENT, 2L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));
    }

    @Test
    void getForBookerPAST() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(bookingRepository.findByBooker_idPast(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingDtoWithItemUser> bookingListRes = service.getForBooker(BookingStatusRequest.PAST, 2L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }

    @Test
    void getForBookerFUTURE() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(bookingRepository.findByBooker_idFuture(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingDtoWithItemUser> bookingListRes = service.getForBooker(BookingStatusRequest.FUTURE, 2L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));
    }

    @Test
    void getForBookerWAITING() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(bookingRepository.findByBooker_idAndStatus(anyLong(),anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingDtoWithItemUser> bookingListRes = service.getForBooker(BookingStatusRequest.WAITING, 2L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }

    @Test
    void getForBookerREJECTED() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userBooker));

        when(bookingRepository.findByBooker_idAndStatus(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        List<BookingDtoWithItemUser> bookingListRes = service.getForBooker(BookingStatusRequest.REJECTED, 2L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }


    //owner
    @Test
    void getForOwnerAll() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userOwner));

        when(bookingRepository.findByOwner_id(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        when(itemService.countByOwner_id(anyLong()))
                .thenReturn(1L);

        List<BookingDtoWithItemUser> bookingListRes = service.getForOwner(BookingStatusRequest.ALL, 1L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));
    }

    @Test
    void getForOwnerCURRENT() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userOwner));

        when(bookingRepository.findByOwner_idCurrent(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        when(itemService.countByOwner_id(anyLong()))
                .thenReturn(1L);

        List<BookingDtoWithItemUser> bookingListRes = service.getForOwner(BookingStatusRequest.CURRENT, 1L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }

    @Test
    void getForOwnerPAST() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userOwner));

        when(bookingRepository.findByOwner_idPast(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        when(itemService.countByOwner_id(anyLong()))
                .thenReturn(1L);

        List<BookingDtoWithItemUser> bookingListRes = service.getForOwner(BookingStatusRequest.PAST, 1L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }

    @Test
    void getForOwnerFUTURE() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userOwner));

        when(bookingRepository.findByOwner_idFuture(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        when(itemService.countByOwner_id(anyLong()))
                .thenReturn(1L);

        List<BookingDtoWithItemUser> bookingListRes = service.getForOwner(BookingStatusRequest.FUTURE, 1L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));
    }

    @Test
    void getForOwnerWAITING() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userOwner));

        when(bookingRepository.findByOwner_idAndStatus(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        when(itemService.countByOwner_id(anyLong()))
                .thenReturn(1L);

        List<BookingDtoWithItemUser> bookingListRes = service.getForOwner(BookingStatusRequest.WAITING, 1L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }

    @Test
    void getForOwnerREJECTED() {
        List<Booking> bookingList = List.of(
                new Booking(
                        1L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.WAITING),
                new Booking(
                        2L,
                        createDateStart,
                        createDateEnd,
                        item,
                        userBooker,
                        BookingStatus.APPROVED));

        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toDTO(userOwner));

        when(bookingRepository.findByOwner_idAndStatus(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookingList));

        when(itemService.countByOwner_id(anyLong()))
                .thenReturn(1L);

        List<BookingDtoWithItemUser> bookingListRes = service.getForOwner(BookingStatusRequest.REJECTED, 1L,0,2);

        assertThat(bookingList.size(),equalTo(bookingListRes.size()));
        assertThat(bookingList.get(0).getId(),equalTo(bookingListRes.get(0).getId()));
        assertThat(bookingList.get(1).getId(),equalTo(bookingListRes.get(1).getId()));


    }


}
