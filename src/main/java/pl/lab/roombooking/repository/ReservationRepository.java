package pl.lab.roombooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lab.roombooking.model.Reservation;
import pl.lab.roombooking.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repozytorium Spring Data JPA dla encji Reservation.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // TODO (Zadanie 1): Zdefiniuj metodę pochodną od nazwy zwracającą wszystkie rezerwacje
    // dla wskazanej sali posortowane rosnąco po czasie rozpoczęcia:
    // List<Reservation> findByRoomIdOrderByStartTimeAsc(Long roomId);

    // TODO (Zadanie 2): Napisz zapytanie JPQL (@Query) sprawdzające, czy dla danej sali istnieje
    // potwierdzona rezerwacja (status = ReservationStatus.CONFIRMED), której przedział czasowy
    // nachodzi na [startTime, endTime].
    // Wskazówka matematyczna: dwa przedziały [A, B] i [C, D] nachodzą na siebie, gdy A < D oraz B > C.
    /*
    @Query("""
            SELECT COUNT(r) > 0 FROM Reservation r
            WHERE r.room.id = :roomId
              AND r.status = pl.lab.roombooking.model.ReservationStatus.CONFIRMED
              AND r.startTime < :endTime
              AND r.endTime > :startTime
            """)
    boolean existsOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    */

    // TODO (Zadanie 4): Zdefiniuj metodę pochodną zwracającą rezerwacje o określonym statusie
    // posortowane rosnąco po startTime:
    // List<Reservation> findByStatusOrderByStartTimeAsc(ReservationStatus status);
}
