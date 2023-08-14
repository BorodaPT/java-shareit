package ru.practicum.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.exception.ExceptionBadRequest;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewRequest(ItemRequestDto itemRequestDto, long userId) {
        if (itemRequestDto.getDescription() == null) {
            throw new ExceptionBadRequest("Создание запроса", "Описание не должно быть пустым");
        }
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequestId(long idRequest, long userId) {
        return get("/" + idRequest, userId);
    }

    public ResponseEntity<Object> getAllUsersRequests(long userId, Integer start, Integer size) {
        if (start != null && size != null) {
            if (start < 0 || size < 1) {
                throw new ExceptionBadRequest("Получение страницы запросов", "Некорректные параметры");
            } else {
                return get("/all?from={from}&size={size}", userId, Map.of(
                        "from", start,
                        "size", size));
            }
        } else {
            return get("/all", userId);
        }

    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("", userId);
    }
}
