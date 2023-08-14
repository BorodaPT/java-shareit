package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ExceptionBadRequest;
import ru.practicum.exception.ExceptionNotFound;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestWithItemDTO;
import ru.practicum.user.UserDto.UserDTO;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Transactional
    @Override
    public ItemRequestDto saveNewRequest(ItemRequestDto itemRequestDto, long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest itemRequestRes = requestRepository.save(itemRequest);

        itemRequestRes.setCreated(itemRequestRes.getCreated().withNano(0));
        return RequestMapper.toDTO(itemRequestRes);
        //return RequestMapper.toDTO(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestWithItemDTO getRequestId(long id, long userId) {
        ItemRequest itemRequest = requestRepository.findById(id).orElseThrow(() -> new ExceptionNotFound("получение itemRequest","Request not found"));
        UserDTO user = userService.getUser(userId);
        List<ItemDto> items = itemService.getByRequestId(id);

        return RequestMapper.toDTOWithItem(itemRequest,items);
    }

    @Override
    public List<ItemRequestWithItemDTO> getUserRequests(long userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        Iterable<ItemRequest> itemRequests = requestRepository.findByRequestorId(userId);
        List<ItemRequestWithItemDTO> itemRequestWithItemDTOS = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestWithItemDTOS.add(RequestMapper.toDTOWithItem(itemRequest, itemService.getByRequestId(itemRequest.getId())));
        }
        return itemRequestWithItemDTOS;
    }

    @Override
    public List<ItemRequestWithItemDTO> getAllUsersRequests(Long userId, Integer idStartRequest, Integer size) {
        List<ItemRequest> itemRequests;
        if (idStartRequest != null && size != null) {
            itemRequests = requestRepository.findAll(userId, PageRequest.of(idStartRequest, size)).getContent();
        } else {
            itemRequests = requestRepository.findAllRequest(userId);
        }
        List<ItemRequestWithItemDTO> itemRequestWithItemDTOS = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestWithItemDTOS.add(RequestMapper.toDTOWithItem(itemRequest, itemService.getByRequestId(itemRequest.getId())));
        }
        return itemRequestWithItemDTOS;
    }

}
