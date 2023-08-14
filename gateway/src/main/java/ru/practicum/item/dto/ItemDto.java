package ru.practicum.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotEmpty
    @Size(max = 200, message = "Превышена максимальная длина описания(200)")
    private String name;

    @NotEmpty
    @Size(max = 200, message = "Превышена максимальная длина описания(200)")
    private String description;

    @JsonProperty(value = "available")
    @NotNull
    private Boolean isAvailable;

    private Long owner;

    private Long requestId;

}
