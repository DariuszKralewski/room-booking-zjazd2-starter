package pl.lab.roombooking.dto;

import pl.lab.roombooking.model.Room;

public record RoomResponse(
        Long id,
        String name,
        String building,
        int floor,
        int capacity,
        boolean hasProjector,
        boolean available
) {
    public static RoomResponse from(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getBuilding(),
                room.getFloor(),
                room.getCapacity(),
                room.isHasProjector(),
                room.isAvailable()
        );
    }
}
