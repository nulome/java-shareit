package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    String SELECT_BOOKING_BY_BOOKER_SQL = "SELECT * FROM bookings b WHERE b.booker_id = ?1 AND b.item_id = ?2 " +
            "AND b.end_time < ?3 AND b.status = ?4 LIMIT 1 ";
    String UPDATE_BOOKING_STATUS_SQL = "update Booking b set b.status = :status where b.id = :id";
    String SELECT_BASE_OWNER_STATE_SQL = "SELECT * FROM bookings b LEFT JOIN items i ON i.id = b.item_id ";
    String SELECT_BY_OWNER_SQL = SELECT_BASE_OWNER_STATE_SQL + "WHERE i.owner_id = ?1 ORDER BY b.start_time DESC ";
    String SELECT_BY_OWNER_STATUS_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "WHERE i.owner_id = ?1 AND b.status = ?2 ORDER BY b.start_time DESC ";
    String SELECT_BY_OWNER_CURRENT_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "WHERE i.owner_id = ?1 AND b.start_time < ?2 AND b.end_time > ?2 ORDER BY b.start_time DESC ";
    String SELECT_BY_OWNER_PAST_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "WHERE i.owner_id = ?1 AND b.end_time < ?2 ORDER BY b.start_time DESC ";
    String SELECT_BY_OWNER_FUTURE_SQL = SELECT_BASE_OWNER_STATE_SQL +
            "WHERE i.owner_id = ?1 AND b.start_time > ?2 ORDER BY b.start_time DESC ";

    Optional<Booking> getBookingById(int id);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(int itemId, StatusBooking status);

    @Query(value = SELECT_BOOKING_BY_BOOKER_SQL, nativeQuery = true)
    Optional<Booking> getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(int userId, int itemId,
                                                                         ZonedDateTime time, String status);

    @Modifying
    @Query(UPDATE_BOOKING_STATUS_SQL)
    void updateStatusBooking(@Param(value = "id") int id, @Param(value = "status") StatusBooking status);

    List<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int userId, StatusBooking status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, ZonedDateTime before, ZonedDateTime after);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int userId, ZonedDateTime after);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int userId, ZonedDateTime before);

    @Query(value = SELECT_BY_OWNER_SQL, nativeQuery = true)
    List<Booking> getBookingsByOwnerItem(int userId);

    @Query(value = SELECT_BY_OWNER_STATUS_SQL, nativeQuery = true)
    List<Booking> getBookingsByOwnerItemAndStatus(int userId, String status);

    @Query(value = SELECT_BY_OWNER_CURRENT_SQL, nativeQuery = true)
    List<Booking> getBookingsByOwnerCurrent(int userId, ZonedDateTime dateTime);

    @Query(value = SELECT_BY_OWNER_PAST_SQL, nativeQuery = true)
    List<Booking> getBookingsByOwnerPast(int userId, ZonedDateTime dateTime);

    @Query(value = SELECT_BY_OWNER_FUTURE_SQL, nativeQuery = true)
    List<Booking> getBookingsByOwnerFuture(int userId, ZonedDateTime dateTime);


}
