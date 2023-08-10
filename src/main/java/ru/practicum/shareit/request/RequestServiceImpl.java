package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExceptionBadRequest;
import ru.practicum.shareit.exception.ExceptionNotFound;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserDto.UserDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
        if (itemRequestDto.getDescription() == null) {
            throw new ExceptionBadRequest("Создание запроса", "Описание не должно быть пустым");
        }
        User user = UserMapper.toUser(userService.getUser(userId));
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest.setCreated(LocalDateTime.now());
        return RequestMapper.toDTO(requestRepository.save(itemRequest));
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
        if (idStartRequest != null && size != null) {
            if (idStartRequest < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            }
        }
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
