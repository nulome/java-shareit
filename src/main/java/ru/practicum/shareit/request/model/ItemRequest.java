package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items_request")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    @ToString.Exclude
    private User requestor;
    @Column(name = "created_time")
    @Type(type = "java.time.ZonedDateTime")
    private ZonedDateTime created;
    @OneToMany
    @JoinColumn(name = "request_id")
    List<Item> listItem;
}