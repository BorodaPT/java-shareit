package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class RequestServiceImplTest {

    private RequestService service;

    private RequestRepository requestRepository;

    private UserService userService;

    private ItemService itemService;

    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        requestRepository = Mockito.mock(RequestRepository.class);
        itemService = Mockito.mock(ItemService.class);
        userService = Mockito.mock(UserService.class);
        service = new RequestServiceImpl(requestRepository, itemService, userService);
        created = LocalDateTime.now().withNano(0);
    }

    @Test
    void saveNewRequest() {
        UserDTO userDTO = new UserDTO(1L, "Пушкин", "push@mail.ru");
        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);

        ItemRequest itemRequest = new ItemRequest(1L,"testSave", UserMapper.toUser(userDTO), created);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"testSave", 1L, created);

        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto requestDtoRes = service.saveNewRequest(itemRequestDto, 1L);

        assertThat(requestDtoRes.getId(), notNullValue());
        assertThat(requestDtoRes.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requestDtoRes.getRequestor(), equalTo(itemRequestDto.getRequestor()));
        assertThat(requestDtoRes.getCreated(), equalTo(itemRequestDto.getCreated()));
    }

    @Test
    void getRequestId() {
        UserDTO userDTO = new UserDTO(1L, "Пушкин", "push@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L,"testSave", UserMapper.toUser(userDTO), created);
        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestWithItemDTO itemRequestWithItemDTO = service.getRequestId(1L,1L);
        assertThat(itemRequestWithItemDTO.getId(), equalTo(1L));
        assertThat(itemRequestWithItemDTO.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestWithItemDTO.getRequestor(), equalTo(itemRequest.getRequestor().getId()));
        assertThat(itemRequestWithItemDTO.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void getUserRequests() {
        UserDTO userDTO = new UserDTO(1L, "Пушкин", "push@mail.ru");

        List<ItemRequest> requests = List.of(
                new ItemRequest(1L,"testGet", UserMapper.toUser(userDTO), created),
                new ItemRequest(2L,"testGet1", UserMapper.toUser(userDTO), created));

        List<ItemDto> itemWithBookingDTOList = List.of(
                new ItemDto(1L,"item1","item1Test",true,2L,2L),
                new ItemDto(2L,"item2","item2Test",true,2L,2L));

        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);
        when(requestRepository.findByRequestorId(anyLong()))
                .thenReturn(requests);
        when(itemService.getByRequestId(anyLong())).thenReturn(itemWithBookingDTOList);

        List<ItemRequestWithItemDTO> itemRequestWithItemDTOS = service.getUserRequests(2L);
        assertThat(itemRequestWithItemDTOS.size(), equalTo(requests.size()));
        assertThat(itemRequestWithItemDTOS.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getId(), equalTo(requests.get(1).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(0).getId(), equalTo(itemWithBookingDTOList.get(0).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(1).getId(), equalTo(itemWithBookingDTOList.get(1).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(0).getDescription(), equalTo(itemWithBookingDTOList.get(0).getDescription()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(1).getDescription(), equalTo(itemWithBookingDTOList.get(1).getDescription()));
    }

    @Test
    void getAllUsersRequestsWithPage() {
        UserDTO userDTO = new UserDTO(1L, "Пушкин", "push@mail.ru");

        List<ItemRequest> requests = List.of(
                new ItemRequest(1L,"testGet", UserMapper.toUser(userDTO), created),
                new ItemRequest(2L,"testGet1", UserMapper.toUser(userDTO), created));

        List<ItemDto> itemWithBookingDTOList = List.of(
                new ItemDto(1L,"item1","item1Test",true,2L,2L),
                new ItemDto(2L,"item2","item2Test",true,2L,2L));

        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);
        when(requestRepository.findAll(anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(requests));
        when(itemService.getByRequestId(anyLong()))
                .thenReturn(itemWithBookingDTOList);

        List<ItemRequestWithItemDTO> itemRequestWithItemDTOS = service.getAllUsersRequests(1L,0,2);

        assertThat(itemRequestWithItemDTOS.size(), equalTo(requests.size()));
        assertThat(itemRequestWithItemDTOS.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getId(), equalTo(requests.get(1).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(0).getId(), equalTo(itemWithBookingDTOList.get(0).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(1).getId(), equalTo(itemWithBookingDTOList.get(1).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(0).getDescription(), equalTo(itemWithBookingDTOList.get(0).getDescription()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(1).getDescription(), equalTo(itemWithBookingDTOList.get(1).getDescription()));

    }

    @Test
    void getAllUsersRequestsWithOutPage() {
        UserDTO userDTO = new UserDTO(1L, "Пушкин", "push@mail.ru");

        List<ItemRequest> requests = List.of(
                new ItemRequest(1L,"testGet", UserMapper.toUser(userDTO), created),
                new ItemRequest(2L,"testGet1", UserMapper.toUser(userDTO), created));

        List<ItemDto> itemWithBookingDTOList = List.of(
                new ItemDto(1L,"item1","item1Test",true,2L,2L),
                new ItemDto(2L,"item2","item2Test",true,2L,2L));

        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);
        when(requestRepository.findAllRequest(anyLong()))
                .thenReturn(requests);
        when(itemService.getByRequestId(anyLong()))
                .thenReturn(itemWithBookingDTOList);

        List<ItemRequestWithItemDTO> itemRequestWithItemDTOS = service.getAllUsersRequests(1L,null,null);

        assertThat(itemRequestWithItemDTOS.size(), equalTo(requests.size()));
        assertThat(itemRequestWithItemDTOS.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getId(), equalTo(requests.get(1).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(0).getId(), equalTo(itemWithBookingDTOList.get(0).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(1).getId(), equalTo(itemWithBookingDTOList.get(1).getId()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(0).getDescription(), equalTo(itemWithBookingDTOList.get(0).getDescription()));
        assertThat(itemRequestWithItemDTOS.get(1).getItems().get(1).getDescription(), equalTo(itemWithBookingDTOList.get(1).getDescription()));

    }

}
