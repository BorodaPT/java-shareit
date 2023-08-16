package ru.practicum.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.model.Item;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestWithItemDTO;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestIntegrationTest {

    private final RequestService service;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final EntityManager entityManager;

    private static ItemRequestDto itemRequestDtoNew;

    @BeforeAll
    static void beforeAll() {
        LocalDateTime created = LocalDateTime.now().withNano(0);
        itemRequestDtoNew = new ItemRequestDto("def", 1L, created);
    }

    @Test
    void saveRequest() {
        userRepository.save(new User(1L, "Пушкин", "push@mail.ru"));
        ItemRequestDto requestDto = service.saveNewRequest(itemRequestDtoNew, 1L);
        TypedQuery<ItemRequest> query = entityManager.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest requestRes = query.setParameter("id", requestDto.getId()).getSingleResult();

        assertThat(requestDto.getId(), notNullValue());
        assertThat(requestDto.getDescription(), equalTo(requestRes.getDescription()));
        assertThat(requestDto.getRequestor(), equalTo(requestRes.getRequestor().getId()));
        assertThat(requestDto.getCreated(), equalTo(requestRes.getCreated()));
    }

    @Test
    void getRequestId() {
        userRepository.save(new User(1L, "Пушкин", "push@mail.ru"));
        userRepository.save(new User(2L, "Пушкин", "push1@mail.ru"));
        service.saveNewRequest(itemRequestDtoNew, 1L);
        ItemRequestDto requestDto1 = service.saveNewRequest(itemRequestDtoNew, 2L);

        ItemRequestWithItemDTO itemDTO = service.getRequestId(2L,2L);

        assertThat(itemDTO.getDescription(), equalTo(requestDto1.getDescription()));
        assertThat(itemDTO.getRequestor(), equalTo(requestDto1.getRequestor()));
        assertThat(itemDTO.getCreated(), equalTo(requestDto1.getCreated()));

    }

    @Test
    void getUserRequests() {
        userRepository.save(new User(1L, "Пушкин", "push@mail.ru"));
        User user2 = userRepository.save(new User(2L, "Пушкин", "push2@mail.ru"));

        ItemRequestDto itemRequestDto1 = service.saveNewRequest(itemRequestDtoNew, 1L);
        ItemRequestDto itemRequestDto2 =service.saveNewRequest(itemRequestDtoNew, 1L);
        ItemRequestDto itemRequestDto3 =service.saveNewRequest(itemRequestDtoNew, 1L);

        Item item1 = itemRepository.save(new Item(null, "item1", "item1test", true, user2, 1L));
        Item item2 = itemRepository.save(new Item(null, "item2", "item2test", true, user2, 2L));

        List<ItemRequestWithItemDTO> itemDTOList = service.getUserRequests(1L);

        assertThat(itemDTOList.size(),equalTo(3));
        assertThat(itemDTOList.get(0).getId(), equalTo(itemRequestDto1.getId()));
        assertThat(itemDTOList.get(1).getId(), equalTo(itemRequestDto2.getId()));
        assertThat(itemDTOList.get(2).getId(), equalTo(itemRequestDto3.getId()));
        assertThat(itemDTOList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(itemDTOList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDTOList.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(itemDTOList.get(1).getItems().get(0).getDescription(), equalTo(item2.getDescription()));
    }

    @Test
    void getAllUsersRequestsWithOutPage() {
        userRepository.save(new User(1L, "Пушкин", "push@mail.ru"));
        User user2 = userRepository.save(new User(2L, "Пушкин", "push2@mail.ru"));
        ItemRequestDto itemRequestDto1 = service.saveNewRequest(itemRequestDtoNew, 1L);
        itemRequestDtoNew.setCreated(LocalDateTime.now().plusHours(1).withNano(0));
        ItemRequestDto itemRequestDto2 = service.saveNewRequest(itemRequestDtoNew, 1L);
        itemRequestDtoNew.setCreated(LocalDateTime.now().plusHours(2).withNano(0));
        ItemRequestDto itemRequestDto3 = service.saveNewRequest(itemRequestDtoNew, 1L);
        service.saveNewRequest(itemRequestDtoNew, 2L);

        Item item1 = itemRepository.save(new Item(null, "item1", "item1test", true, user2, 1L));
        Item item2 = itemRepository.save(new Item(null, "item2", "item2test", true, user2, 2L));

        List<ItemRequestWithItemDTO> itemDTOList = service.getAllUsersRequests(2L, null, null);

        assertThat(itemDTOList.size(),equalTo(3));
        assertThat(itemDTOList.get(0).getId(), equalTo(itemRequestDto1.getId()));
        assertThat(itemDTOList.get(1).getId(), equalTo(itemRequestDto2.getId()));
        assertThat(itemDTOList.get(2).getId(), equalTo(itemRequestDto3.getId()));
        assertThat(itemDTOList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(itemDTOList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDTOList.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(itemDTOList.get(1).getItems().get(0).getDescription(), equalTo(item2.getDescription()));

    }

    @Test
    void getAllUsersRequestsWithPage() {
        userRepository.save(new User(1L, "Пушкин", "push@mail.ru"));
        User user2 = userRepository.save(new User(2L, "Пушкин", "push2@mail.ru"));
        ItemRequestDto itemRequestDto1 = service.saveNewRequest(itemRequestDtoNew, 1L);
        itemRequestDtoNew.setCreated(LocalDateTime.now().plusHours(1).withNano(0));
        ItemRequestDto itemRequestDto2 = service.saveNewRequest(itemRequestDtoNew, 1L);
        itemRequestDtoNew.setCreated(LocalDateTime.now().plusHours(2).withNano(0));
        ItemRequestDto itemRequestDto3 = service.saveNewRequest(itemRequestDtoNew, 1L);
        service.saveNewRequest(itemRequestDtoNew, 2L);

        Item item1 = itemRepository.save(new Item(null, "item1", "item1test", true, user2, 1L));
        Item item2 = itemRepository.save(new Item(null, "item2", "item2test", true, user2, 3L));

        List<ItemRequestWithItemDTO> itemDTOList = service.getAllUsersRequests(2L, 0, 1);

        assertThat(itemDTOList.size(),equalTo(1));
        assertThat(itemDTOList.get(0).getId(), equalTo(itemRequestDto1.getId()));
        assertThat(itemDTOList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(itemDTOList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
    }

}
