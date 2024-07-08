package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.commentDto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT new ru.practicum.shareit.item.commentDto.CommentShortDto(cm.id, cm.text, cm.author.name, cm.item.id," +
            " cm.created) FROM Comment AS cm " +
            "WHERE cm.item.id IN ?1 " +
            "ORDER BY cm.created DESC")
    List<CommentShortDto> findAllByItemIdIn(List<Integer> listItemId);
}
