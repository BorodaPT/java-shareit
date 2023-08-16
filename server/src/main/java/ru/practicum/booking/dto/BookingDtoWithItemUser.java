package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.ItemInfo;
import ru.practicum.user.UserDto.UserInfo;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoWithItemUser {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemInfo item;

    private UserInfo booker;

    private BookingStatus status;

}
