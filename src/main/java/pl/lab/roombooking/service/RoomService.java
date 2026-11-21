package pl.lab.roombooking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lab.roombooking.dto.CreateRoomRequest;
import pl.lab.roombooking.dto.RoomResponse;
import pl.lab.roombooking.dto.UpdateRoomRequest;
import pl.lab.roombooking.exception.ResourceNotFoundException;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.RoomRepository;

import java.util.List;

/**
 * Warstwa logiki biznesowej dla sal, oparta już na trwałym repozytorium Spring Data JPA.
 */
@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomResponse> searchRooms(Integer minCapacity, Boolean hasProjector) {
        return roomRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .filter(room -> minCapacity == null || room.getCapacity() >= minCapacity)
                .filter(room -> hasProjector == null || room.isHasProjector() == hasProjector)
                .map(RoomResponse::from)
                .toList();
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + id));
        return RoomResponse.from(room);
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        Room room = new Room(
                request.name(),
                request.building(),
                request.floor(),
                request.capacity(),
                request.hasProjector(),
                true
        );
        Room saved = roomRepository.save(room);
        return RoomResponse.from(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + id));

        room.setName(request.name());
        room.setBuilding(request.building());
        room.setFloor(request.floor());
        room.setCapacity(request.capacity());
        room.setHasProjector(request.hasProjector());
        room.setAvailable(request.available());

        return RoomResponse.from(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nie znaleziono sali o ID: " + id);
        }
        roomRepository.deleteById(id);
    }
}
