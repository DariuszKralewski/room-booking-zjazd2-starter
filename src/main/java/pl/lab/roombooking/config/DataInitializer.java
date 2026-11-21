package pl.lab.roombooking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.lab.roombooking.model.Reservation;
import pl.lab.roombooking.model.Room;
import pl.lab.roombooking.repository.ReservationRepository;
import pl.lab.roombooking.repository.RoomRepository;

import java.time.LocalDateTime;

/**
 * Inicjalizuje bazę danych przykładowymi salami oraz rezerwacjami przy starcie aplikacji.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public DataInitializer(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            return;
        }

        Room salaA101 = roomRepository.save(new Room("Sala A101", "Budynek A", 1, 24, true, true));
        Room aulaB204 = roomRepository.save(new Room("Aula B204", "Budynek B", 2, 120, true, true));
        roomRepository.save(new Room("Laboratorium C012", "Budynek C", 0, 16, false, false));

        LocalDateTime tomorrowMorning = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        reservationRepository.save(new Reservation(
                salaA101,
                "Jan Kowalski",
                "jan.kowalski@uczelnia.pl",
                "Wykład z Programowania w Javie",
                tomorrowMorning,
                tomorrowMorning.plusHours(2)
        ));

        reservationRepository.save(new Reservation(
                aulaB204,
                "Anna Nowak",
                "anna.nowak@uczelnia.pl",
                "Seminarium dyplomowe",
                tomorrowMorning.plusHours(3),
                tomorrowMorning.plusHours(5)
        ));
    }
}
