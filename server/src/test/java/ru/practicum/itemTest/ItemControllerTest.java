package ru.practicum.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.dto.BookingForItemDTO;
import ru.practicum.item.ItemController;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.CommentDTO;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithBookingDTO;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService service;

    @Autowired
    private ObjectMapper mapper;

    private LocalDateTime createDateStart;

    private LocalDateTime createDateEnd;

    @BeforeEach
    void serUp() {
        createDateStart = LocalDateTime.now().withNano(0);
        createDateEnd = LocalDateTime.now().plusDays(6).withNano(0);
    }

    @Test
    void create() throws Exception  {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "описание",
                true,
                1L,
                null);
        when(service.saveNewItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);
        String jsonRequest = mapper.writeValueAsString(itemDto);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.available").value(itemDto.getIsAvailable()));

        verify(service, times(1)).saveNewItem(any(ItemDto.class), anyLong());
    }

    @Test
    void edit() throws Exception  {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "описание",
                true,
                1L,
                null);
        when(service.saveItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);
        String jsonRequest = mapper.writeValueAsString(itemDto);
        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.available").value(itemDto.getIsAvailable()));

        verify(service, times(1)).saveItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @Test
    void getItemById() throws Exception  {
        ItemWithBookingDTO itemDto = new ItemWithBookingDTO(
                1L,
                "name",
                "описание",
                true,
                1L,
                1L,
                new BookingForItemDTO(1L,1L,createDateStart,createDateEnd),
                null,
                List.of(new CommentDTO(1L,"test","Push",LocalDateTime.now().withNano(0))));

        when(service.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.available").value(itemDto.getIsAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(1L))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(1L))
                .andExpect(jsonPath("$.lastBooking.start").value(createDateStart.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.lastBooking.end").value(createDateEnd.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.comments[0].id").value(1L))
                .andExpect(jsonPath("$.comments[0].text").value("test"));

        verify(service, times(1)).getItem(anyLong(), anyLong());

    }

    @Test
    void getItems() throws Exception  {

        ItemWithBookingDTO itemDto = new ItemWithBookingDTO(
                1L,
                "name",
                "описание",
                true,
                1L,
                1L,
                new BookingForItemDTO(1L,1L,createDateStart,createDateEnd),
                null,
                List.of(new CommentDTO(1L,"test","Push",LocalDateTime.now().withNano(0))));
        ItemWithBookingDTO itemDto2 = new ItemWithBookingDTO(
                1L,
                "name",
                "описание",
                true,
                1L,
                2L,
                new BookingForItemDTO(2L,2L,createDateStart,createDateEnd),
                null,
                List.of(new CommentDTO(1L,"test2","Push2",LocalDateTime.now().withNano(0))));

        when(service.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto,itemDto2));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1)
                        .param("from","0")
                        .param("size","2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.[0].available").value(itemDto.getIsAvailable()))
                .andExpect(jsonPath("$.[0].lastBooking.id").value(1L))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId").value(1L))
                .andExpect(jsonPath("$.[0].lastBooking.start").value(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.[0].lastBooking.end").value(itemDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.[0].comments[0].id").value(1L))
                .andExpect(jsonPath("$.[0].comments[0].text").value("test"));

        verify(service, times(1)).getItems(anyLong(), anyInt(), anyInt());

    }

    @Test
    void search() throws Exception  {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "описание",
                true,
                1L,
                null);
        when(service.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1)
                .param("from","0")
                .param("size","1")
                .param("text","опи"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].owner").value(itemDto.getOwner()))
                .andExpect(jsonPath("$.[0].available").value(itemDto.getIsAvailable()));
        verify(service, times(1)).search(anyString(), anyInt(), anyInt());

    }

    @Test
    void getWithComment() throws Exception  {
        CommentDTO commentDTO = new CommentDTO(1L,"test","Push",LocalDateTime.now().withNano(0));
        when(service.createComment(any(CommentDTO.class), anyLong(), anyLong()))
                .thenReturn(commentDTO);
        mvc.perform(post("/items/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1)
                .content(mapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDTO.getId()))
                .andExpect(jsonPath("$.text").value(commentDTO.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDTO.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDTO.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)));
        verify(service, times(1)).createComment(any(CommentDTO.class), anyLong(), anyLong());
    }


}
