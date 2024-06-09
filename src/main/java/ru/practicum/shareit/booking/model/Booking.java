package ru.practicum.shareit.booking.model;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.ZonedDateTime;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_time")
    @Type(type = "java.time.ZonedDateTime")
    private ZonedDateTime start;
    @Column(name = "end_time")
    @Type(type = "java.time.ZonedDateTime")
    private ZonedDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;
    @Enumerated(EnumType.STRING)
    private StatusBooking status;
}
