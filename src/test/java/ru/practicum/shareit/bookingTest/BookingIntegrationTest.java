package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusRequest;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingIntegrationTest {

    private final BookingService bookingService;

    private final ItemService itemService;

    private final UserService userService;

    private final EntityManager entityManager;

    private LocalDateTime createDateStart;

    private LocalDateTime createDateEnd;

    private UserDTO userDTO;

    private BookingDto bookingDto;

    private ItemDto itemDtoDef;

    @BeforeEach
    void serUp() {
        createDateStart = LocalDateTime.now().plusDays(1).withNano(0);
        createDateEnd = LocalDateTime.now().plusDays(6).withNano(0);
        userDTO = userService.saveUser(new UserDTO(1L, "Пушкин", "push@mail.ru"));
        userService.saveUser(new UserDTO(2L, "Пушкин2", "push2@mail.ru"));
        bookingDto = new BookingDto(
                1L,
                createDateStart,
                createDateEnd,
                1L,
                1L,
                BookingStatus.WAITING);
        ItemDto itemDtoDef = new ItemDto(1L,"name","описание",true,1L, null);
        itemService.saveNewItem(itemDtoDef, 1l);
    }

    @Test
    void saveNewBooking() {
        BookingDtoWithItemUser bookingDef = bookingService.saveNewBooking(bookingDto,2L);
        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingRes = query.setParameter("id", bookingDef.getId()).getSingleResult();

        assertThat(bookingDef.getId(), notNullValue());
        assertThat(bookingDef.getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingRes.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(bookingDef.getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingRes.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(bookingDef.getStatus(), equalTo(bookingRes.getStatus()));
    }

    @Test
    void setStatus() {
        bookingService.saveNewBooking(bookingDto,2L);
        BookingDtoWithItemUser bookingDef = bookingService.setStatus(1L,1L, true);
        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingRes = query.setParameter("id", bookingDef.getId()).getSingleResult();
        assertThat(bookingDef.getStatus(), equalTo(bookingRes.getStatus()));

    }

    @Test
    void getBooking() {
        BookingDtoWithItemUser booking = bookingService.saveNewBooking(bookingDto,2L);
        BookingDtoWithItemUser bookingDef = bookingService.getBooking(1L,2L);

        assertThat(bookingDef.getId(), equalTo(booking.getId()));
        assertThat(bookingDef.getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(bookingDef.getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(bookingDef.getStatus(), equalTo(booking.getStatus()));
    }

    //booker

    @Test
    void getForBookerCurrent() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForBooker(BookingStatusRequest.CURRENT, 2L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(2L));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getForBookerPast() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForBooker(BookingStatusRequest.PAST, 2L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(2L));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.APPROVED));

    }

    @Test
    void getForBookerFuture() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForBooker(BookingStatusRequest.FUTURE, 2L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getForBookerWAITING() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForBooker(BookingStatusRequest.WAITING, 2L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getForBookerREJECTED() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.REJECTED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForBooker(BookingStatusRequest.REJECTED, 2L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(2L));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getForBookerAll() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.REJECTED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForBooker(BookingStatusRequest.ALL, 2L,0,2);

        assertThat(listBookingRes.size(), equalTo(2));
        assertThat(listBookingRes.get(0).getId(), equalTo(1L));
        assertThat(listBookingRes.get(1).getId(), equalTo(2L));
        assertThat(listBookingRes.get(1).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(1).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(1).getStatus(), equalTo(BookingStatus.REJECTED));
    }

    //Owner
    @Test
    void getForOwnerAll() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.REJECTED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForOwner(BookingStatusRequest.ALL, 1L,0,2);

        assertThat(listBookingRes.size(), equalTo(2));
        assertThat(listBookingRes.get(0).getId(), equalTo(1L));
        assertThat(listBookingRes.get(1).getId(), equalTo(2L));
        assertThat(listBookingRes.get(1).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(1).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(1).getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getForOwnerCURRENT() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForOwner(BookingStatusRequest.CURRENT, 1L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(2L));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getForOwnerPAST() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForOwner(BookingStatusRequest.PAST, 1L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(2L));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getForOwnerFUTURE() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForOwner(BookingStatusRequest.FUTURE, 1L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getForOwnerWAITING() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.APPROVED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForOwner(BookingStatusRequest.WAITING, 1L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getForOwnerREJECTED() {
        bookingService.saveNewBooking(bookingDto,2L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(6).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2).withNano(0);

        entityManager.createNativeQuery("INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES (?,?,?,?,?)")
                .setParameter(1,  startDate)
                .setParameter(2, endDate)
                .setParameter(3, 1L)
                .setParameter(4, 2L)
                .setParameter(5, BookingStatus.REJECTED.name())
                .executeUpdate();

        List<BookingDtoWithItemUser> listBookingRes = bookingService.getForOwner(BookingStatusRequest.REJECTED, 1L,0,1);

        assertThat(listBookingRes.size(), equalTo(1));
        assertThat(listBookingRes.get(0).getId(), equalTo(2L));
        assertThat(listBookingRes.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(startDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(endDate.format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(listBookingRes.get(0).getStatus(), equalTo(BookingStatus.REJECTED));
    }

}
