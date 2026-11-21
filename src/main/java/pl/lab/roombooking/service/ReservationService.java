package pl.lab.roombooking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lab.roombooking.dto.CreateReservationRequest;
import pl.lab.roombooking.dto.ReservationResponse;
import pl.lab.roombooking.exception.BookingConflictException;
import pl.lab.roombooking.exception.ResourceNotFoundException;
import pl.lab.roombooking.model.Reservation;
import pl.lab.roombooking.model.ReservationStatus;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.ReservationRepository;
import pl.lab.roombooking.repository.RoomRepository;

import java.util.List;

/**
 * Warstwa logiki biznesowej zarządzająca rezerwacjami sal.
 * Adnotacja @Transactional gwarantuje atomowość operacji zapisu w bazie danych
 * oraz utrzymuje aktywną sesję Hibernate podczas odczytu relacji LAZY (reservation.getRoom()).
 */
@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public List<ReservationResponse> getAllReservations(ReservationStatus status) {
        // TODO (Zadanie 4): Jeśli status != null, zwróć tylko rezerwacje o podanym statusie
        // (wykorzystaj metodę pochodną z ReservationRepository).
        return reservationRepository.findAll(Sort.by(Sort.Direction.ASC, "startTime")).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    // TODO (Zadanie 1): Pobierz wszystkie rezerwacje dla wskazanej sali (roomId).
    // Najpierw sprawdź, czy sala istnieje (roomRepository.existsById(roomId)) — jeśli nie, rzuć ResourceNotFoundException!
    public List<ReservationResponse> getReservationsForRoom(Long roomId) {
        throw new UnsupportedOperationException("TODO: Zaimplementuj getReservationsForRoom w Zadaniu 1");
    }

    // TODO (Zadanie 2): Utwórz nową rezerwację z pełną walidacją biznesową:
    // 1. Sprawdź, czy startTime.isBefore(endTime) — w przeciwnym razie rzuć IllegalArgumentException.
    // 2. Pobierz salę po request.roomId() lub rzuć ResourceNotFoundException.
    // 3. Sprawdź, czy sala jest dostępna (room.isAvailable()) — jeśli nie, rzuć BookingConflictException.
    // 4. Sprawdź, czy nie ma kolizji terminów (existsOverlappingReservation) — jeśli jest, rzuć BookingConflictException.
    // 5. Zapisz nową rezerwację i zwróć ReservationResponse.
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        throw new UnsupportedOperationException("TODO: Zaimplementuj createReservation w Zadaniu 2");
    }

    // TODO (Zadanie 3): Pobierz pojedynczą rezerwację po ID (lub rzuć ResourceNotFoundException).
    public ReservationResponse getReservationById(Long id) {
        throw new UnsupportedOperationException("TODO: Zaimplementuj getReservationById w Zadaniu 3");
    }

    // TODO (Zadanie 3): Anuluj rezerwację o podanym ID:
    // - Jeśli nie istnieje -> rzuć ResourceNotFoundException.
    // - Jeśli ma już status CANCELLED -> rzuć BookingConflictException.
    // - Zmień status na CANCELLED, zapisz i zwróć ReservationResponse.
    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        throw new UnsupportedOperationException("TODO: Zaimplementuj cancelReservation w Zadaniu 3");
    }
}
