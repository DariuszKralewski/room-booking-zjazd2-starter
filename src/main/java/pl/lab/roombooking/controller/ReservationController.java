package pl.lab.roombooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lab.roombooking.dto.CreateReservationRequest;
import pl.lab.roombooking.dto.ReservationResponse;
import pl.lab.roombooking.model.ReservationStatus;
import pl.lab.roombooking.service.ReservationService;

import java.util.List;

/**
 * Kontroler REST obsługujący rezerwacje sal (/api/reservations).
 */
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Zarządzanie rezerwacjami sal")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Pobiera listę wszystkich rezerwacji (z opcjonalnym filtrowaniem po statusie)")
    public ResponseEntity<List<ReservationResponse>> getAllReservations(
            @RequestParam(required = false) ReservationStatus status
    ) {
        return ResponseEntity.ok(reservationService.getAllReservations(status));
    }

    // TODO (Zadanie 2 i 5): Dodaj endpoint POST /api/reservations
    // - Odbierz @Valid @RequestBody CreateReservationRequest.
    // - Zwróć 201 Created z nagłówkiem Location (/api/reservations/{id}) oraz ciałem ReservationResponse.

    // TODO (Zadanie 3): Dodaj endpoint GET /api/reservations/{id}
    // - Zwróć 200 OK z obiektem ReservationResponse (gdy nie istnieje, serwis rzuci ResourceNotFoundException -> 404).

    // TODO (Zadanie 3): Dodaj endpoint PATCH /api/reservations/{id}/cancel
    // - Wywołaj reservationService.cancelReservation(id) i zwróć 200 OK ze zaktualizowanym ReservationResponse.
}
