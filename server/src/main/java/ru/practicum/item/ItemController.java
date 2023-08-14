package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDTO;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithBookingDTO;

import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
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
                                     @RequestBody CommentDTO commentDTO) {
        return itemService.createComment(commentDTO,idItem, userId);
    }

}
