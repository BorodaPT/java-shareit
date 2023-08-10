package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.saveNewItem(itemDto,userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable("itemId") Long idItem,
                        @RequestBody ItemDto itemDto) {
        return itemService.saveItem(itemDto,userId,idItem);
    }

    @GetMapping
    public List<ItemWithBookingDTO> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", required = false) Integer start,
                                             @RequestParam(name = "size", required = false) Integer size) {
        return itemService.getItems(userId, start, size);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDTO get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("itemId") Long idItem) {
        return itemService.getItem(idItem, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "from", required = false) Integer start,
                                @RequestParam(name = "size", required = false) Integer size,
                                @RequestParam(name = "text", required = false) String text) {
        return itemService.search(text, start, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO getWithComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("itemId") Long idItem,
                                     @Valid @RequestBody CommentDTO commentDTO) {
        return itemService.createComment(commentDTO,idItem, userId);
    }

}
