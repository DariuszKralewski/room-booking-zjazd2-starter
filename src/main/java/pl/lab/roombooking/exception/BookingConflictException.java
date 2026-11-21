package pl.lab.roombooking.exception;

/**
 * Wyjątek rzucany przy naruszeniu reguł biznesowych rezerwacji
 * (np. kolizja terminów, próba rezerwacji niedostępnej sali lub ponowne anulowanie).
 * Powinien zostać zmapowany na kod odpowiedzi HTTP 409 Conflict.
 */
public class BookingConflictException extends RuntimeException {

    public BookingConflictException(String message) {
        super(message);
    }
}
