package pl.lab.roombooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO żądania utworzenia nowej rezerwacji sali (POST /api/reservations).
 * TODO (Zadanie 5): Dodaj adnotacje Bean Validation (@NotNull, @NotBlank, @Email, @Size, @Future).
 */
public record CreateReservationRequest(
        Long roomId,
        String reservedBy,
        String contactEmail,
        String purpose,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
