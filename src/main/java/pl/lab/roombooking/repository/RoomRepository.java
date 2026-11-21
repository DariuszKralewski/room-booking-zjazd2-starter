package pl.lab.roombooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lab.roombooking.model.Room;

/**
 * Repozytorium Spring Data JPA dla encji Room.
 * Spring automatycznie generuje implementację podstawowych metod CRUD (findAll, findById, save, deleteById itd.).
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
}
