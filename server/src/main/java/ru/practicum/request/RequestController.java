package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestWithItemDTO;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.saveNewRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDTO getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("requestId") Long idRequest) {
        return requestService.getRequestId(idRequest, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDTO> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", required = false) Integer start,
                                               @RequestParam(name = "size", required = false) Integer size) {
        return requestService.getAllUsersRequests(userId,start,size);
    }

    @GetMapping
    public List<ItemRequestWithItemDTO> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getUserRequests(userId);
    }


}
