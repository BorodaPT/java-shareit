package ru.practicum.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDto {

    private Long id;

    private String description;

    private Long requestor;

    private LocalDateTime created;

    public ItemRequestDto(Long id, String description, Long requestor, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }

    public ItemRequestDto(String description, Long requestor, LocalDateTime created) {
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
