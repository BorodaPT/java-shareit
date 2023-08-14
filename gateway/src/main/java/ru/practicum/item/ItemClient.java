package ru.practicum.item;

import ru.practicum.exception.ExceptionBadRequest;
import ru.practicum.item.dto.CommentDTO;
import ru.practicum.item.dto.ItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewItem(ItemDto itemDto, long userId) {
       return post("", userId, itemDto);
    }

    public ResponseEntity<Object> saveItem(ItemDto itemDto, long userId, long idItem) {
       return patch("/" + idItem, userId, itemDto);
    }

    public ResponseEntity<Object> getItems(long userId, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            } else {
                return get("?from={from}&size={size}", userId, Map.of(
                        "from", start,
                        "size", size));
            }
        } else {
            return get("", userId);
        }
    }

    public ResponseEntity<Object> getItem(long idItem, long userId) {
        return get("/" + idItem, userId);
    }

    public ResponseEntity<Object> search(long userId, String text, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            } else {
                return get("/search?text={text}&from={from}&size={size}", userId, Map.of(
                        "text", text,
                        "from", start,
                        "size", size));
            }
        } else {
            return get("/search?text={text}", userId, Map.of("text", text));
        }
    }

    public ResponseEntity<Object> createComment(CommentDTO commentDTO, long idItem, long userId) {
       return post("/" + idItem + "/comment", userId, commentDTO);
    }
}
