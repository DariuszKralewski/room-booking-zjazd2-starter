package pl.lab.roombooking.model;

import jakarta.persistence.*;

/**
 * Encja JPA reprezentująca salę w bazie danych (tabela 'rooms').
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String building;

    private int floor;

    private int capacity;

    private boolean hasProjector;

    private boolean available;

    public Room() {
    }

    public Room(String name, String building, int floor, int capacity, boolean hasProjector, boolean available) {
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
