package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne
    @ToString.Exclude
    private User owner;
    @Column(name = "request_id")
    private Integer request;
    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;
}
