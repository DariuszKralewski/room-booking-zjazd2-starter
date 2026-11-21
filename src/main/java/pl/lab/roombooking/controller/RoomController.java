package pl.lab.roombooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lab.roombooking.dto.CreateRoomRequest;
import pl.lab.roombooking.dto.ReservationResponse;
import pl.lab.roombooking.dto.RoomResponse;
import pl.lab.roombooking.dto.UpdateRoomRequest;
import pl.lab.roombooking.service.ReservationService;
import pl.lab.roombooking.service.RoomService;

import java.net.URI;
import java.util.List;

/**
 * Kontroler REST dla sal (/api/rooms) — gotowy CRUD oparty na bazie danych JPA.
 */
@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Zarządzanie salami (Spring Data JPA)")
public class RoomController {

    private final RoomService roomService;
    private final ReservationService reservationService;

    public RoomController(RoomService roomService, ReservationService reservationService) {
        this.roomService = roomService;
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Pobiera listę sal z opcjonalnym filtrowaniem")
    public ResponseEntity<List<RoomResponse>> getAllRooms(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Boolean hasProjector
    ) {
        return ResponseEntity.ok(roomService.searchRooms(minCapacity, hasProjector));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera szczegóły sali po ID")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // TODO (Zadanie 5): Dodaj adnotację @Valid przed @RequestBody CreateRoomRequest
    @PostMapping
    @Operation(summary = "Tworzy nową salę")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        RoomResponse created = roomService.createRoom(request);
        URI location = URI.create("/api/rooms/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // TODO (Zadanie 5): Dodaj adnotację @Valid przed @RequestBody UpdateRoomRequest
    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje istniejącą salę")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @RequestBody UpdateRoomRequest request
    ) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuwa salę o podanym ID")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // TODO (Zadanie 1): Dodaj endpoint GET /api/rooms/{roomId}/reservations
    // zwracający listę rezerwacji dla wskazanej sali (wykorzystaj reservationService.getReservationsForRoom(roomId)).
}
