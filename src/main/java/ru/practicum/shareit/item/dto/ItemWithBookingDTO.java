package ru.practicum.shareit.item.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingDTO {

    private Long id;

    private String name;

    private String description;

    @JsonProperty(value = "available")
    private Boolean isAvailable;

    private Long owner;

    private BookingForItemDTO lastBooking;

    private BookingForItemDTO nextBooking;

    private List<CommentDTO> comments;

}
