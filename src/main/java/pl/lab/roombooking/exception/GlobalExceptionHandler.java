package pl.lab.roombooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Globalny handler wyjątków przechwytujący błędy ze wszystkich kontrolerów REST
 * i zwracający ustrukturyzowane odpowiedzi zgodne ze standardem RFC 9457 (ProblemDetail).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Nie znaleziono zasobu");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // TODO (Zadanie 6): Dodaj obsługę wyjątku BookingConflictException -> 409 Conflict (ProblemDetail)

    // TODO (Zadanie 6): Dodaj obsługę wyjątku IllegalArgumentException -> 400 Bad Request (ProblemDetail)

    // TODO (Zadanie 6): Dodaj obsługę błędu walidacji MethodArgumentNotValidException -> 400 Bad Request
    // z dodatkową właściwością "errors" zawierającą mapę <nazwaPola, komunikatBłędu>.
}
