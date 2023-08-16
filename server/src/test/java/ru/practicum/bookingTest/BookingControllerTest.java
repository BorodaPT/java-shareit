package ru.practicum.bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.BookingController;
import ru.practicum.booking.BookingService;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoWithItemUser;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.booking.model.BookingStatusRequest;
import ru.practicum.item.dto.ItemInfo;
import ru.practicum.item.model.Item;
import ru.practicum.user.UserDto.UserInfo;
import ru.practicum.user.model.User;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    @Autowired
    private ObjectMapper mapper;

    private LocalDateTime createDateStart;

    private LocalDateTime createDateEnd;

    private Item item;

    @BeforeEach
    void serUp() {
        createDateStart = LocalDateTime.now().withNano(0);
        createDateEnd = LocalDateTime.now().plusDays(6).withNano(0);
        item = new Item(
                1L,
                "name",
                "описание",
                true,
                new User(1L, "first", "fir@mail.ru"),
                null);
    }

    @Test
    void create() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                createDateStart,
                createDateEnd,
                1L,
                1L,
                BookingStatus.WAITING);
        BookingDtoWithItemUser bookingDtoWithItemUser = new BookingDtoWithItemUser(
                1L,
                createDateStart,
                createDateEnd,
                new ItemInfo(
                        1L,
                        "отвертка"),
                new UserInfo(1L),
                BookingStatus.WAITING);

        when(service.saveNewBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingDtoWithItemUser);

        String jsonRequest = mapper.writeValueAsString(bookingDto);
        mvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoWithItemUser.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoWithItemUser.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDtoWithItemUser.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item.id").value(bookingDtoWithItemUser.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoWithItemUser.getBooker().getId()));

        verify(service, times(1)).saveNewBooking(any(BookingDto.class), anyLong());
    }

    @Test
    void setStatus() throws Exception {

        BookingDtoWithItemUser bookingDtoWithItemUser = new BookingDtoWithItemUser(1L,createDateStart, createDateEnd, new ItemInfo(1L, "отвертка"), new UserInfo(1L), BookingStatus.APPROVED);
        when(service.setStatus(anyLong(),anyLong(),anyBoolean()))
                .thenReturn(bookingDtoWithItemUser);
        mvc.perform(patch("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1)
                .param("approved","true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoWithItemUser.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoWithItemUser.getStatus().toString()));
        verify(service, times(1)).setStatus(anyLong(),anyLong(),anyBoolean());

    }

    @Test
    void getBooking() throws Exception {
        BookingDtoWithItemUser bookingDtoWithItemUser = new BookingDtoWithItemUser(1L,createDateStart, createDateEnd, new ItemInfo(1L, "отвертка"), new UserInfo(1L), BookingStatus.APPROVED);
        when(service.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDtoWithItemUser);

        mvc.perform(get("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoWithItemUser.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoWithItemUser.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDtoWithItemUser.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item.id").value(bookingDtoWithItemUser.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoWithItemUser.getBooker().getId()));
        verify(service, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void getBookingForBooker() throws Exception {
        List<BookingDtoWithItemUser> bookings = List.of(
                new BookingDtoWithItemUser(1L,createDateStart, createDateEnd, new ItemInfo(1L, "отвертка"), new UserInfo(1L), BookingStatus.APPROVED),
                new BookingDtoWithItemUser(2L,createDateStart, createDateEnd, new ItemInfo(2L, "самовар"), new UserInfo(1L), BookingStatus.APPROVED));
        when(service.getForBooker(any(BookingStatusRequest.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mvc.perform(get("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1)
                .param("state","ALL")
                .param("from","0")
                .param("size","2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookings.get(0).getBooker().getId()));
        verify(service, times(1)).getForBooker(any(BookingStatusRequest.class), anyLong(), anyInt(), anyInt());

    }

    @Test
    void getBookingForOwner() throws Exception {
        List<BookingDtoWithItemUser> bookings = List.of(
                new BookingDtoWithItemUser(1L,createDateStart, createDateEnd, new ItemInfo(1L, "отвертка"), new UserInfo(1L), BookingStatus.APPROVED),
                new BookingDtoWithItemUser(2L,createDateStart, createDateEnd, new ItemInfo(2L, "самовар"), new UserInfo(1L), BookingStatus.APPROVED));
        when(service.getForOwner(any(BookingStatusRequest.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1)
                        .param("state","ALL")
                        .param("from","0")
                        .param("size","2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookings.get(0).getBooker().getId()));
        verify(service, times(1)).getForOwner(any(BookingStatusRequest.class), anyLong(), anyInt(), anyInt());
    }

}
