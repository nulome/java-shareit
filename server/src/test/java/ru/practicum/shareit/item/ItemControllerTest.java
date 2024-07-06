package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemWithDateBookingResponse;
import ru.practicum.shareit.item.dto.PatchItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.related.Constants.CONTROLLER_ITEM_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom random = new EasyRandom();

    private User user;
    private int userId;
    private ItemResponse itemResponse;
    private ItemWithDateBookingResponse itemWithDateBookingResponse;


    @BeforeEach
    void setUp() {
        userId = 1;
        User user = random.nextObject(User.class);
        user.setId(userId);
        itemResponse = random.nextObject(ItemResponse.class);
        itemWithDateBookingResponse = random.nextObject(ItemWithDateBookingResponse.class);
    }

    @Test
    @SneakyThrows
    void createItem_whenRightRequest_thenStatusCreated() {
        CreateItemRequestDto createItemRequestDto = random.nextObject(CreateItemRequestDto.class);
        createItemRequestDto.setRequestId(null);
        when(itemService.createItem(userId, createItemRequestDto))
                .thenReturn(itemResponse);

        mvc.perform(post(CONTROLLER_ITEM_PATH)
                        .content(mapper.writeValueAsString(createItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable()), Boolean.class));

        verify(itemService).createItem(anyInt(), any(CreateItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void readItem_whenRightPathVariable_thenItemById() {
        int itemId = 7;
        itemWithDateBookingResponse.setId(itemId);
        when(itemService.readItem(userId, itemId))
                .thenReturn(itemWithDateBookingResponse);

        mvc.perform(get("/items/" + itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithDateBookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemWithDateBookingResponse.getName()), String.class));

        verify(itemService).readItem(anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItemsByTextSearch_whenRightResponse_thenStatusOkWithJsonSizeOne() {
        String search = "UGLD";
        List<ItemResponse> listActual = List.of(itemResponse);
        when(itemService.getItemsByTextSearch(1, search, 0, 3))
                .thenReturn(listActual);

        mvc.perform(get(CONTROLLER_ITEM_PATH + "/search?text=" + search + "&from=0&size=3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(itemService).getItemsByTextSearch(1, search, 0, 3);
    }

    @Test
    @SneakyThrows
    void changeItem_whenRightPatchName_thenResponseWithPatch() {
        String patch = "patchName";
        PatchItemRequestDto patchItemRequestDto = new PatchItemRequestDto();
        patchItemRequestDto.setName(patch);

        when(itemService.changeItem(anyInt(), anyInt(), any(PatchItemRequestDto.class)))
                .thenAnswer(i -> {
                    itemResponse.setName(i.getArgument(2, PatchItemRequestDto.class).getName());
                    return itemResponse;
                });

        mvc.perform(patch(CONTROLLER_ITEM_PATH + "/1")
                        .content(mapper.writeValueAsString(patchItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(patch), String.class));

        verify(itemService).changeItem(anyInt(), anyInt(), any(PatchItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void createComment_whenRightRequest_thenStatusOk() {
        CreateCommentRequestDto createCommentRequestDto = random.nextObject(CreateCommentRequestDto.class);
        CommentResponse commentResponse = random.nextObject(CommentResponse.class);
        when(itemService.createComment(anyInt(), anyInt(), any(CreateCommentRequestDto.class)))
                .thenReturn(commentResponse);

        mvc.perform(post(CONTROLLER_ITEM_PATH + "/1/comment")
                        .content(mapper.writeValueAsString(createCommentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(REQUEST_HEADER_USER_KEY, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName()), String.class));

        verify(itemService).createComment(anyInt(), anyInt(), any(CreateCommentRequestDto.class));
    }

    @Test
    @SneakyThrows
    void createComment_thenBadRequestSharerUserId_whenStatusBadRequest() {
        CreateCommentRequestDto createCommentRequestDto = random.nextObject(CreateCommentRequestDto.class);

        mvc.perform(post(CONTROLLER_ITEM_PATH + "/1/comment")
                        .content(mapper.writeValueAsString(createCommentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createComment(anyInt(), anyInt(), any(CreateCommentRequestDto.class));
    }
}