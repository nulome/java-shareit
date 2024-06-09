package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User author;
    private ZonedDateTime created;
}
