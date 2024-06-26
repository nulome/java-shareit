package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    String SELECT_BOOKING_BY_BOOKER_SQL = "SELECT * FROM bookings b WHERE b.booker_id = ?1 AND b.item_id = ?2 " +
            "AND b.end_time < ?3 AND b.status = ?4 LIMIT 1 ";
    String UPDATE_BOOKING_STATUS_SQL = "update Booking b set b.status = :status where b.id = :id";
    String SELECT_BASE_OWNER_STATE_SQL = "SELECT b FROM Booking b JOIN b.item it WHERE it.owner.id = ?1 ";
    String SELECT_BY_OWNER_SQL = SELECT_BASE_OWNER_STATE_SQL + "ORDER BY b.start DESC ";
    String SELECT_BY_OWNER_STATUS_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "AND b.status = ?2 ORDER BY b.start DESC ";
    String SELECT_BY_OWNER_CURRENT_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "AND ?2 BETWEEN b.start AND b.end ORDER BY b.start DESC ";
    String SELECT_BY_OWNER_PAST_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "AND b.end < ?2 ORDER BY b.start DESC ";
    String SELECT_BY_OWNER_FUTURE_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "AND b.start > ?2 ORDER BY b.start DESC ";

    Optional<Booking> getBookingById(int id);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(int itemId, StatusBooking status);

    @Query(value = SELECT_BOOKING_BY_BOOKER_SQL, nativeQuery = true)
    Optional<Booking> getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(int userId, int itemId,
                                                                         ZonedDateTime time, String status);

    @Modifying
    @Query(UPDATE_BOOKING_STATUS_SQL)
    void updateStatusBooking(@Param(value = "id") int id, @Param(value = "status") StatusBooking status);

    Page<Booking> findAllByBookerIdOrderByStartDesc(int userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int userId, StatusBooking status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, ZonedDateTime before,
                                                                             ZonedDateTime after, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int userId, ZonedDateTime after, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int userId, ZonedDateTime before, Pageable pageable);

    @Query(value = SELECT_BY_OWNER_SQL)
    Page<Booking> getBookingsByOwnerItem(int userId, Pageable pageable);

    @Query(value = SELECT_BY_OWNER_STATUS_SQL)
    Page<Booking> getBookingsByOwnerItemAndStatus(int userId, StatusBooking status, Pageable pageable);

    @Query(value = SELECT_BY_OWNER_CURRENT_SQL)
    Page<Booking> getBookingsByOwnerCurrent(int userId, ZonedDateTime dateTime, Pageable pageable);

    @Query(value = SELECT_BY_OWNER_PAST_SQL)
    Page<Booking> getBookingsByOwnerPast(int userId, ZonedDateTime dateTime, Pageable pageable);

    @Query(value = SELECT_BY_OWNER_FUTURE_SQL)
    Page<Booking> getBookingsByOwnerFuture(int userId, ZonedDateTime dateTime, Pageable pageable);


}
