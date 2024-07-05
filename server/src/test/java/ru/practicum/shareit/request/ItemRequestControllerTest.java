package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.related.Constants.CONTROLLER_REQUEST_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom random = new EasyRandom();

    private int userId = 1;

    @Test
    @SneakyThrows
    void createdRequest_whenCreateDto_thenResponseIsCorrect() {
        CreateItemRequestReqDto request = random.nextObject(CreateItemRequestReqDto.class);
        ItemRequestResponse response = random.nextObject(ItemRequestResponse.class);

        when(itemRequestService.createdRequest(anyInt(), any(CreateItemRequestReqDto.class)))
                .thenAnswer(invocationOnMock -> {
                    response.setDescription(invocationOnMock.getArgument(1,
                            CreateItemRequestReqDto.class).getDescription());
                    response.setId(1);
                    return response;
                });

        mvc.perform(post(CONTROLLER_REQUEST_PATH)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userId), Integer.class))
                .andExpect(jsonPath("$.description", is(request.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(response.getCreated().toString()), String.class));

        verify(itemRequestService).createdRequest(anyInt(), any(CreateItemRequestReqDto.class));

    }

    @Test
    @SneakyThrows
    void getRequests_whenRequest_whenStatusOkWithCallingMethod() {
        List<ItemRequestResponse> response = List.of(random.nextObject(ItemRequestResponse.class));

        when(itemRequestService.getRequests(anyInt()))
                .thenReturn(response);

        mvc.perform(get(CONTROLLER_REQUEST_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(itemRequestService).getRequests(userId);
    }

    @Test
    @SneakyThrows
    void getRequestsAll_whenRequest_whenStatusOkWithCallingMethod() {
        List<ItemRequestResponse> response = List.of(random.nextObject(ItemRequestResponse.class),
                random.nextObject(ItemRequestResponse.class));

        when(itemRequestService.getRequestsAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get(CONTROLLER_REQUEST_PATH + "/all?from=2&size=2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(itemRequestService).getRequestsAll(userId, 2, 2);
    }

    @Test
    @SneakyThrows
    void getItemRequest_whenRightPathVariable_thenItemReqById() {
        ItemRequestResponse response = random.nextObject(ItemRequestResponse.class);
        int requestId = 3;

        when(itemRequestService.getItemRequest(requestId, userId))
                .thenReturn(response);

        mvc.perform(get(CONTROLLER_REQUEST_PATH + "/" + requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(response.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(response.getCreated().toString()), String.class));

        verify(itemRequestService).getItemRequest(requestId, userId);
    }

}