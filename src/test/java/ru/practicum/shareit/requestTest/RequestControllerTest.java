package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService service;

    @Autowired
    private ObjectMapper mapper;

    private ItemRequestDto requestDtoDef;
    private ItemRequestDto requestDtoNew;

    private ItemRequestWithItemDTO itemRequestWithItemDTO1;

    private ItemRequestWithItemDTO itemRequestWithItemDTO2;

    private List<ItemRequestWithItemDTO> requestDTOListWithItemRequestor1;



    @BeforeEach
    void setUp() {
        LocalDateTime createDate = LocalDateTime.now().withNano(0);
        requestDtoDef = new ItemRequestDto(1L,"описание", 1L , createDate);
        requestDtoNew = new ItemRequestDto("описание", 1L , createDate);
        itemRequestWithItemDTO1 = new ItemRequestWithItemDTO(1L,"описание", 1L , createDate,
                List.of(
                new ItemDto(1L,"item1","item1", true, 2L,1L),
                new ItemDto(1L,"item2","item2", true, 3L,1L)));
        itemRequestWithItemDTO2 = new ItemRequestWithItemDTO(2L,"описание", 1L , createDate,
                List.of(new ItemDto(3L,"item3","item3", true, 4L,2L),
                        new ItemDto(4L,"item4","item4", true, 5L,2L)));
        requestDTOListWithItemRequestor1 = new ArrayList<>();
        requestDTOListWithItemRequestor1.add(itemRequestWithItemDTO1);
        requestDTOListWithItemRequestor1.add(itemRequestWithItemDTO2);

    }

    @Test
    void create() throws Exception  {
        when(service.saveNewRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(requestDtoDef);
        String jsonRequest = mapper.writeValueAsString(requestDtoNew);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1)
                        .content(jsonRequest))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(requestDtoDef.getId()))
                        .andExpect(jsonPath("$.description").value(requestDtoDef.getDescription()))
                        .andExpect(jsonPath("$.requestor").value(requestDtoDef.getRequestor()))
                        .andExpect(jsonPath("$.created").value(requestDtoDef.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)));

        verify(service, times(1)).saveNewRequest(any(ItemRequestDto.class), anyLong());
    }

    @Test
    void getUserRequest() throws Exception {
        when(service.getUserRequests(anyLong()))
                .thenReturn(requestDTOListWithItemRequestor1);
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id",1))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[*]").exists())
                        .andExpect(jsonPath("$.[*]").isNotEmpty())
                        .andExpect(jsonPath("$.[*]").isArray())
                        .andExpect(jsonPath("$.size()").value(2));
        verify(service, times(1)).getUserRequests(anyLong());
    }

    @Test
    void getAllRequest() throws Exception {
        when(service.getAllUsersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(requestDTOListWithItemRequestor1);
        mvc.perform(get("/requests/all")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",2)
                        .param("from","0")
                        .param("size","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].requestor").value(requestDTOListWithItemRequestor1.get(0).getRequestor()))
                .andExpect(jsonPath("$.[1].requestor").value(requestDTOListWithItemRequestor1.get(1).getRequestor()))
        ;
        verify(service, times(1)).getAllUsersRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getRequestId() throws Exception {
        when(service.getRequestId(anyLong(), anyLong()))
                .thenReturn(itemRequestWithItemDTO1);
        mvc.perform(get("/requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestWithItemDTO1.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestWithItemDTO1.getDescription()))
                .andExpect(jsonPath("$.requestor").value(itemRequestWithItemDTO1.getRequestor()))
                .andExpect(jsonPath("$.created").value(itemRequestWithItemDTO1.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.items[0].id").value(itemRequestWithItemDTO1.getItems().get(0).getId()))
                .andExpect(jsonPath("$.items[1].id").value(itemRequestWithItemDTO1.getItems().get(1).getId()));
        verify(service, times(1)).getRequestId(anyLong(),anyLong());
    }

}
