package ru.practicum.shareit.related;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public final class UtilityClasses {

    public static Pageable createPageRequest(Integer from, Integer size) {
        int number = from / size;
        return PageRequest.of(number, size);
    }
}
