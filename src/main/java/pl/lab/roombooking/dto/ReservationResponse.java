package pl.lab.roombooking.dto;

import pl.lab.roombooking.model.Reservation;
import pl.lab.roombooking.model.ReservationStatus;

import java.time.LocalDateTime;

/**
 * DTO odpowiedzi zwracającej szczegóły rezerwacji wraz z podstawowymi danymi sali.
 */
public record ReservationResponse(
        Long id,
        Long roomId,
        String roomName,
        String reservedBy,
        String contactEmail,
        String purpose,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoom().getId(),
                reservation.getRoom().getName(),
                reservation.getReservedBy(),
                reservation.getContactEmail(),
                reservation.getPurpose(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
