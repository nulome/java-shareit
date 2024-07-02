package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.PatchUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.Map;

import static ru.practicum.shareit.related.Constants.CONTROLLER_USER_PATH;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = CONTROLLER_USER_PATH;

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(CreateUserRequestDto requestDto) {
        return post("", requestDto);
    }

    public ResponseEntity<Object> readUser(int userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> updateUser(UserRequestDto requestDto) {
        return put("", requestDto);
    }

    public ResponseEntity<Object> deleteUser(int userId) {
        return delete("/" + userId);
    }

    public ResponseEntity<Object> getUsers(int number, int size) {
        Map<String, Object> parameters = Map.of(
                "number", number,
                "size", size
        );
        return get("?number={number}&size={size}", parameters);
    }

    public ResponseEntity<Object> changeUser(int userId, PatchUserRequestDto patchDto) {
        return patch("/" + userId, patchDto);
    }

}
