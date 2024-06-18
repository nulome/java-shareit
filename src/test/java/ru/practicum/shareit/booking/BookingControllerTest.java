package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom random = new EasyRandom();
    private int userId;

    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        userId = 1;
        bookingResponse = random.nextObject(BookingResponse.class);
    }

    @Test
    @SneakyThrows
    void createBooking_whenRightRequest_thenStatusCreated() {
        CreateBookingRequestDto createBookingRequestDto = new CreateBookingRequestDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(4), 1);
        when(bookingService.createBooking(userId, createBookingRequestDto))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class));

        verify(bookingService).createBooking(anyInt(), any(CreateBookingRequestDto.class));
    }

    @Test
    @SneakyThrows
    void createBooking_whenBadRequestAvailableNull_thenFailValidation() {
        CreateBookingRequestDto createBookingRequestDto = new CreateBookingRequestDto(LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusDays(4), 1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(anyInt(), any(CreateBookingRequestDto.class));
    }

    @Test
    @SneakyThrows
    void readBooking_whenRightRequest_thenVerifyMethod() {
        when(bookingService.readBooking(userId, 1)).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class));

        verify(bookingService).readBooking(userId, 1);
    }

    @Test
    @SneakyThrows
    void changeBookingStatus_whenRightRequest_thenVerifyMethod() {
        when(bookingService.changeBookingStatus(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/1?approved=TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class));

        verify(bookingService).changeBookingStatus(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getBookingsByUser_whenRightRequest_thenResponseHasSize() {
        List<BookingResponse> list = List.of(bookingResponse, bookingResponse);
        when(bookingService.getBookingsByUser(userId, "ALL", null, null))
                .thenReturn(list);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));

        verify(bookingService).getBookingsByUser(userId, "ALL", null, null);
    }
}