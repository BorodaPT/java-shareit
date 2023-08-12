package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
public class ItemIntegrationTest {

    private final ItemService itemService;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final EntityManager entityManager;

    private LocalDateTime createDateStart;

    private LocalDateTime createDateEnd;

    private User user;

    @BeforeEach
    void serUp() {
        createDateStart = LocalDateTime.now().withNano(0);
        createDateEnd = LocalDateTime.now().plusDays(6).withNano(0);
        user = userRepository.save(new User(1L, "Пушкин", "push@mail.ru"));
    }

    @Test
    void saveNewItem() {
        ItemDto itemDtoDef = new ItemDto(1L,"name","описание",true,1L, null);
        ItemDto itemDto = itemService.saveNewItem(itemDtoDef, 1L);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemRes = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(itemDtoDef.getId(), notNullValue());
        assertThat(itemDtoDef.getDescription(), equalTo(itemRes.getDescription()));
        assertThat(itemDtoDef.getName(), equalTo(itemRes.getName()));
        assertThat(itemDtoDef.getIsAvailable(), equalTo(itemRes.getIsAvailable()));
    }

    @Test
    void saveItem() {
        ItemDto itemDtoDef = new ItemDto(1L,"name","описание",true,1L, null);
        itemService.saveNewItem(itemDtoDef, 1L);
        ItemDto itemDto = itemService.saveItem(new ItemDto(1L,"name3","описание4",true,1L, null), 1L, 1L);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item requestRes = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getDescription(), equalTo(requestRes.getDescription()));
        assertThat(itemDto.getName(), equalTo(requestRes.getName()));
        assertThat(itemDto.getIsAvailable(), equalTo(requestRes.getIsAvailable()));
    }

    @Test
    void getItem() {
        ItemDto itemDtoDef = itemService.saveNewItem(new ItemDto(1L,"name3","описание4",true,1L, null), 1L);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item requestRes = query.setParameter("id", itemDtoDef.getId()).getSingleResult();
        assertThat(itemDtoDef.getId(), notNullValue());
        assertThat(itemDtoDef.getDescription(), equalTo(requestRes.getDescription()));
        assertThat(itemDtoDef.getName(), equalTo(requestRes.getName()));
        assertThat(itemDtoDef.getIsAvailable(), equalTo(requestRes.getIsAvailable()));
    }

    @Test
    void getItems() {
        ItemDto itemDtoDef = itemService.saveNewItem(new ItemDto(1L,"name1","описание1",true,1L, null), 1L);
        itemService.saveNewItem(new ItemDto(2L,"name2","описание2",true,1L, null), 1L);

        List<ItemWithBookingDTO> itemWithBookingDTOList = itemService.getItems(1,0,1);

        assertThat(itemWithBookingDTOList.get(0).getId(), notNullValue());
        assertThat(itemWithBookingDTOList.get(0).getDescription(), equalTo(itemDtoDef.getDescription()));
        assertThat(itemWithBookingDTOList.get(0).getName(), equalTo(itemDtoDef.getName()));
        assertThat(itemWithBookingDTOList.get(0).getIsAvailable(), equalTo(itemDtoDef.getIsAvailable()));
    }

    @Test
    void search() {
        ItemDto itemDtoDef = itemService.saveNewItem(new ItemDto(1L,"name1","описание1",true,1L, null), 1L);
        itemService.saveNewItem(new ItemDto(2L,"name2","Serv",true,1L, null), 1L);

        List<ItemDto> itemDTOList = itemService.search("оПис",0,1);

        assertThat(itemDTOList.get(0).getId(), notNullValue());
        assertThat(itemDTOList.get(0).getDescription(), equalTo(itemDtoDef.getDescription()));
        assertThat(itemDTOList.get(0).getName(), equalTo(itemDtoDef.getName()));
        assertThat(itemDTOList.get(0).getIsAvailable(), equalTo(itemDtoDef.getIsAvailable()));
    }

    @Test
    void delete() {
        int cntBefore = itemService.getItems(1, null, null).size();
        ItemDto itemDto = itemService.saveNewItem(new ItemDto(1L,"name1","описание1",true,1L, null), 1L);
        assertThat(itemService.getItems(1, null, null).size(), equalTo(cntBefore + 1));
        itemService.delete(itemDto.getId());
        assertThat(itemService.getItems(1, null, null).size(), equalTo(cntBefore));
    }

    @Test
    void createComment() {
        userRepository.save(new User(2L, "Пушкин", "push2@mail.ru"));
        Item item = ItemMapper.toItem(itemService.saveNewItem(new ItemDto(1L,"name1","описание1",true,1L, null), 2L), user);
        bookingRepository.save(new Booking(1L, createDateStart, createDateEnd, item, user, BookingStatus.APPROVED));
        CommentDTO commentDTO = itemService.createComment(new CommentDTO(1L,"test","Push",LocalDateTime.now().withNano(0)), 1L, 1L);
        TypedQuery<Comment> query = entityManager.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment requestRes = query.setParameter("id", commentDTO.getId()).getSingleResult();
        assertThat(commentDTO.getId(), notNullValue());
        assertThat(commentDTO.getText(), equalTo(requestRes.getText()));
        assertThat(commentDTO.getAuthorName(), equalTo(requestRes.getAuthor().getName()));
        assertThat(commentDTO.getCreated().format(DateTimeFormatter.ISO_DATE_TIME), equalTo(requestRes.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)));
    }


}
