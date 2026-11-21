package pl.lab.roombooking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Encja JPA reprezentująca rezerwację sali (tabela 'reservations').
 * Posiada relację wiele-do-jednego (@ManyToOne) z encją Room.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 120)
    private String reservedBy;

    @Column(nullable = false, length = 150)
    private String contactEmail;

    @Column(nullable = false, length = 250)
    private String purpose;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    public Reservation() {
    }

    public Reservation(
            Room room,
            String reservedBy,
            String contactEmail,
            String purpose,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.room = room;
        this.reservedBy = reservedBy;
        this.contactEmail = contactEmail;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ReservationStatus.CONFIRMED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
