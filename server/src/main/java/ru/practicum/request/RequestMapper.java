package ru.practicum.request;

import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestWithItemDTO;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static ItemRequestDto toDTO(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                itemRequestDto.getCreated());
    }

    public static List<ItemRequestDto> toDTO(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(toDTO(itemRequest));
        }
        return  itemRequestDtos;
    }

    public static ItemRequestWithItemDTO toDTOWithItem(ItemRequest itemRequest, List<ItemDto> itemDtos) {
        return new ItemRequestWithItemDTO(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                itemDtos);
    }

}
