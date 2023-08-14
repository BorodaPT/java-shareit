package ru.practicum.item;


import ru.practicum.item.dto.CommentDTO;
import ru.practicum.item.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class GatewayItemController {

    @Autowired
    private ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        return itemClient.saveNewItem(itemDto,userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> edit(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable("itemId") Long idItem,
                        @RequestBody ItemDto itemDto) {
        return itemClient.saveItem(itemDto,userId,idItem);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", required = false) Integer start,
                                             @RequestParam(name = "size", required = false) Integer size) {
        return itemClient.getItems(userId, start, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("itemId") Long idItem) {
        return itemClient.getItem(idItem, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "from", required = false) Integer start,
                                         @RequestParam(name = "size", required = false) Integer size,
                                         @RequestParam(name = "text", required = false) String text) {
        return itemClient.search(userId, text, start, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> getWithComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("itemId") Long idItem,
                                     @Valid @RequestBody CommentDTO commentDTO) {
        return itemClient.createComment(commentDTO,idItem, userId);
    }
}
