package ru.practicum.request.dto;

import ru.practicum.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestWithItemDTO {
    private Long id;

    private String description;

    private Long requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}
