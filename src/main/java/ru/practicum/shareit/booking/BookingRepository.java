package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select book.* " +
            "from bookings as book "+
            "join items as it ON it.id = book.item_id "+
            "where book.id = ?1 AND (it.owner_id = ?2 OR book.booker_id = ?2) ", nativeQuery = true)
    Optional<Booking> findByIdAndOwnerIdORBookerId(Long idBooking, Long idUser);

    //forItem
    @Query(value = "SELECT * FROM BOOKINGS b " +
            "WHERE ITEM_ID = ?1 AND START_DATE <= now() AND status = 'APPROVED' " +
            "ORDER BY START_DATE DESC " +
            "LIMIT 1", nativeQuery = true)
    Booking findLastBookingForItem(Long itemId);

    @Query(value = "SELECT * FROM BOOKINGS b " +
            "WHERE ITEM_ID = ?1 AND START_DATE >= now() AND status = 'APPROVED' " +
            "ORDER BY START_DATE " +
            "LIMIT 1", nativeQuery = true)
    Booking findNextBookingForItem(Long itemId);

    //for booker
    @Query(value = "select book.* " +
            "from bookings as book "+
            "where booker_id = ?1 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByBooker_id(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "where booker_id = ?1 " +
            "and start_date <= now() AND end_date >= now() " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByBooker_idCurrent(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "where booker_id = ?1 " +
            "and status = 'APPROVED' " +
            "and end_date < now() " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByBooker_idPast(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "where booker_id = ?1 " +
            "and start_date > now() " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByBooker_idFuture(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "where booker_id = ?1 " +
            "and status = ?2 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByBooker_idAndStatus(Long id, String bookingStatus);

    //owner
    @Query(value = "select book.* " +
            "from bookings as book "+
            "join items as it ON it.id = book.item_id "+
            "where it.owner_id = ?1 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByOwner_id(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "join items as it ON it.id = book.item_id " +
            "where it.owner_id = ?1 " +
            "and book.start_date <= now() AND book.end_date >= now() " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByOwner_idCurrent(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "join items as it ON it.id = book.item_id "+
            "where it.owner_id = ?1 " +
            "and end_date < now() " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByOwner_idPast(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "join items as it ON it.id = book.item_id "+
            "where it.owner_id = ?1 " +
            "and start_date > now() " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByOwner_idFuture(Long id);

    @Query(value = "select book.* " +
            "from bookings as book "+
            "join items as it ON it.id = book.item_id "+
            "where it.owner_id = ?1 " +
            "and status = ?2 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findByOwner_idAndStatus(Long id, String bookingStatus);

    //comment
    @Query(value = "select book.* " +
            "from bookings as book "+
            "where book.item_id = ?1 AND book.booker_id = ?2 AND start_date <= ?3 " +
            "order by start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findByItem_idAndBooker_idAndStart_dateBefore(Long itemId, Long userId, LocalDateTime dateTime);


}
