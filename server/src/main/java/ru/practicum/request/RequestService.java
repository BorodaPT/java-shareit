package ru.practicum.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestWithItemDTO;

import java.util.List;

@Transactional(readOnly = true)
public interface RequestService {

    @Transactional
    ItemRequestDto saveNewRequest(ItemRequestDto itemRequest, long userId);

    List<ItemRequestWithItemDTO> getUserRequests(long userId);

    List<ItemRequestWithItemDTO> getAllUsersRequests(Long userId, Integer idStartRequest, Integer size);

    ItemRequestWithItemDTO getRequestId(long id, long userId);

}
