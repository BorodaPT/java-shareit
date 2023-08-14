package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class GatewayRequestController {

    @Autowired
    private RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.saveNewRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("requestId") Long idRequest) {
        return requestClient.getRequestId(idRequest, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", required = false) Integer start,
                                               @RequestParam(name = "size", required = false) Integer size) {
        return requestClient.getAllUsersRequests(userId,start,size);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getUserRequests(userId);
    }
}
