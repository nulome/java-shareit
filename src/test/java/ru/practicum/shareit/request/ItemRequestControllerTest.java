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

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom random = new EasyRandom();


    @Test
    @SneakyThrows
    void createdRequest_whenCreateDto_thenResponseIsCorrect() {
        CreateItemRequestReqDto request = random.nextObject(CreateItemRequestReqDto.class);
        ItemRequestResponse response = random.nextObject(ItemRequestResponse.class);
        int userId = 1;

        when(itemRequestService.createdRequest(anyInt(), any(CreateItemRequestReqDto.class)))
                .thenAnswer(invocationOnMock -> {
                    response.setDescription(invocationOnMock.getArgument(1,
                            CreateItemRequestReqDto.class).getDescription());
                    response.setId(1);
                    return response;
                });

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
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
    void createdRequest_whenCreateDto_thenCorrectVerificationRequest() {
        CreateItemRequestReqDto request = random.nextObject(CreateItemRequestReqDto.class);
        request.setDescription("   ");
        ItemRequestResponse response = random.nextObject(ItemRequestResponse.class);
        int userId = 1;

        when(itemRequestService.createdRequest(anyInt(), any(CreateItemRequestReqDto.class)))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).createdRequest(anyInt(), any(CreateItemRequestReqDto.class));

    }

    @Test
    @SneakyThrows
    void getRequests_whenRequest_whenStatusOkWithCallingMethod() {
        List<ItemRequestResponse> response = List.of(random.nextObject(ItemRequestResponse.class));
        int userId = 1;

        when(itemRequestService.getRequests(anyInt()))
                .thenReturn(response);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
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
        int userId = 1;

        when(itemRequestService.getRequestsAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/requests/all?from=2&size=2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
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
        int userId = 1;
        int requestId = 3;

        when(itemRequestService.getItemRequest(requestId, userId))
                .thenReturn(response);

        mvc.perform(get("/requests/" + requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(response.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(response.getCreated().toString()), String.class));

        verify(itemRequestService).getItemRequest(requestId, userId);
    }

}