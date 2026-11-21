package pl.lab.roombooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO żądania utworzenia nowej sali.
 * TODO (Zadanie 5): Dodaj adnotacje Bean Validation (@NotBlank, @Size, @Min, @Positive).
 */
public record CreateRoomRequest(
        String name,
        String building,
        int floor,
        int capacity,
        boolean hasProjector
) {
}
