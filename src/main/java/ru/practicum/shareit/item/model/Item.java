package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
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
    @Cascade(CascadeType.SAVE_UPDATE)
    private User owner;
    @Column(name = "request_id")
    private Integer request;
    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;
}
