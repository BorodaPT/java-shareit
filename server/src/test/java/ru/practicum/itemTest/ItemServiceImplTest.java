package ru.practicum.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.CommentRepository;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.ItemService;
import ru.practicum.item.ItemServiceImpl;
import ru.practicum.item.dto.CommentDTO;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithBookingDTO;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.UserDto.UserDTO;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ItemServiceImplTest {

    private ItemService service;

    private ItemRepository itemRepository;

    private UserService userService;

    private BookingRepository bookingRepository;

    private CommentRepository commentRepository;

    private LocalDateTime createDateStart;

    private LocalDateTime createDateEnd;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        service = new ItemServiceImpl(itemRepository, userService, commentRepository, bookingRepository);
        createDateStart = LocalDateTime.now().withNano(0);
        createDateEnd = LocalDateTime.now().plusDays(6).withNano(0);

        userDTO = new UserDTO(
                1L,
                "Пушкин",
                "push@mail.ru");
    }

    @Test
    void saveNewItem() {
        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);
        ItemDto itemDtoDef = new ItemDto(1L,"name","описание",true,1L, null);
        Item item = new Item(1L,"name","описание",true, UserMapper.toUser(userDTO), null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = service.saveNewItem(itemDtoDef,1L);

        assertThat(itemDto.getDescription(), equalTo(itemDtoDef.getDescription()));
        assertThat(itemDto.getName(), equalTo(itemDtoDef.getName()));
        assertThat(itemDto.getIsAvailable(), equalTo(itemDtoDef.getIsAvailable()));
    }

    @Test
    void saveItem() {
        Item item = new Item(
                1L,
                "name",
                "описание",
                true,
                UserMapper.toUser(userDTO),
                null);

        Item itemRes = new Item(
                1L,
                "name",
                "none",
                true,
                UserMapper.toUser(userDTO),
                null);

        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemRes);


        ItemDto itemDto = new ItemDto(1L,"name","none",true,1L,null);
        ItemDto itemDtoRes = service.saveItem(itemDto, 1L, 1L);

        assertThat(itemDtoRes.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoRes.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoRes.getIsAvailable(), equalTo(itemDto.getIsAvailable()));
    }

    @Test
    void getItem() {
        Item item = new Item(
                1L,
                "name",
                "описание",
                true,
                UserMapper.toUser(userDTO),
                null);
        Booking bookingLast = new Booking(1L,createDateStart,createDateEnd,item,UserMapper.toUser(userDTO), BookingStatus.APPROVED);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByIdItem(anyLong()))
                .thenReturn(null);
        when(bookingRepository.findLastBookingForItem(anyLong()))
                .thenReturn(bookingLast);

        ItemWithBookingDTO itemDtoRes = service.getItem(1L,1L);

        assertThat(itemDtoRes.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoRes.getName(), equalTo(item.getName()));
        assertThat(itemDtoRes.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemDtoRes.getLastBooking().getId(), equalTo(bookingLast.getId()));
        assertThat(itemDtoRes.getLastBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingLast.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(itemDtoRes.getLastBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingLast.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void getItems() {
        List<Item> items = List.of(new Item(
                1L,
                "name",
                "описание",
                true,
                UserMapper.toUser(userDTO),
                null));
        Booking bookingLast = new Booking(1L,createDateStart,createDateEnd,items.get(0),UserMapper.toUser(userDTO), BookingStatus.APPROVED);
        when(itemRepository.findByOwner_idOrderById(anyLong()))
                .thenReturn(items);
        when(bookingRepository.findLastBookingForItem(anyLong()))
                .thenReturn(bookingLast);
        when(commentRepository.findAllByIdItem(anyLong()))
                .thenReturn(null);

        List<ItemWithBookingDTO> itemsRes =  service.getItems(userDTO.getId(), null,null);
        assertThat(itemsRes.get(0).getDescription(), equalTo(items.get(0).getDescription()));
        assertThat(itemsRes.get(0).getName(), equalTo(items.get(0).getName()));
        assertThat(itemsRes.get(0).getIsAvailable(), equalTo(items.get(0).getIsAvailable()));
        assertThat(itemsRes.get(0).getLastBooking().getId(), equalTo(bookingLast.getId()));
        assertThat(itemsRes.get(0).getLastBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingLast.getStart().format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(itemsRes.get(0).getLastBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(bookingLast.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void search() {
        List<Item> items = List.of(new Item(
                1L,
                "name",
                "описание",
                true,
                UserMapper.toUser(userDTO),
                null));
        when(itemRepository.findByNameContainingOrDescriptionContaining(anyString()))
                .thenReturn(items);
        List<ItemDto> itemsRes = service.search("опис",null,null);
        assertThat(itemsRes.get(0).getDescription(), equalTo(items.get(0).getDescription()));
        assertThat(itemsRes.get(0).getName(), equalTo(items.get(0).getName()));
        assertThat(itemsRes.get(0).getIsAvailable(), equalTo(items.get(0).getIsAvailable()));
    }

    @Test
    void delete() {
        Item item = new Item(
                1L,
                "name",
                "описание",
                true,
                UserMapper.toUser(userDTO),
                null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(anyLong());

        service.delete(1L);

        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void createComment() {
        Item item = new Item(
                1L,
                "name",
                "описание",
                true,
                UserMapper.toUser(userDTO),
                null);

        Booking bookingLast = new Booking(
                1L,
                createDateStart,
                createDateEnd,
                item,
                UserMapper.toUser(userDTO),
                BookingStatus.APPROVED);

        Comment comment = new Comment(
                1L,
                "test",
                item,
                UserMapper.toUser(userDTO),
                createDateStart);
        CommentDTO commentDTO = new CommentDTO(
                1L,
                "test",
                userDTO.getName(),
                createDateStart);

        when(userService.getUser(anyLong()))
                .thenReturn(userDTO);

        when(bookingRepository.findByItem_idAndBooker_idAndStart_dateBefore(anyLong(),anyLong(),any(LocalDateTime.class)))
                .thenReturn(Optional.of(bookingLast));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDTO commentDTORes = service.createComment(commentDTO, item.getId(), userDTO.getId());

        assertThat(commentDTORes.getText(), equalTo(comment.getText()));
        assertThat(commentDTORes.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(commentDTORes.getId(), equalTo(comment.getId()));

    }
}
