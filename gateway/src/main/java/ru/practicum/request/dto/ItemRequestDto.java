package ru.practicum.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ItemRequestDto {

    private Long id;

    @Size(max = 200, message = "Превышена максимальная длина описания(200)")
    @NotBlank
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
