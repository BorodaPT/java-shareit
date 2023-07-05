package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        return itemMapper.toDTO(itemService.create(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable("itemId") Long idItem,
                        @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toDTO(itemService.edit(item,idItem,userId));
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId).stream().map(itemMapper::toDTO).collect(toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") Long idItem) {
        return itemMapper.toDTO(itemService.get(idItem));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text", required = false) String text) {
        return itemService.search(text).stream().map(itemMapper::toDTO).collect(toList());
    }







}
