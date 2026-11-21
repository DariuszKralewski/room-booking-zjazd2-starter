package pl.lab.roombooking.exception;

/**
 * Wyjątek rzucany, gdy żądany zasób (np. sala lub rezerwacja) nie istnieje w bazie danych.
 * Powinien zostać zmapowany na kod odpowiedzi HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
