# Room Booking Service — Projekt startowy (Zjazd 2)

Repozytorium startowe do przedmiotu **Programowanie aplikacji webowych w Javie** (Zjazd 2: persystencja danych w Spring Data JPA, relacje encji, deklaratywna walidacja `@Valid` oraz centralna obsługa wyjątków z `ProblemDetail`).

## Wymagania wstępne

- **Java JDK 21** (lub nowsza wersja LTS)
- **Apache Maven 3.9+** (lub wbudowany Maven w IntelliJ IDEA)
- Środowisko programistyczne: **IntelliJ IDEA**, **VS Code** (z *Extension Pack for Java*) lub **Eclipse**

## Uruchomienie projektu

1. Sklonuj repozytorium i otwórz folder projektu w swoim IDE (jako projekt Maven).
2. Uruchom klasę `pl.lab.roombooking.RoomBookingApplication` z poziomu IDE **lub** wykonaj w terminalu:
   ```bash
   mvn spring-boot:run
   ```
3. Po uruchomieniu aplikacji otwórz w przeglądarce:
   - **Testy połączenia (Healthcheck):** http://localhost:8080/api/health
   - **Dokumentacja i konsola testowa Swagger UI:** http://localhost:8080/swagger-ui.html
   - **Konsola bazy danych H2:** http://localhost:8080/h2-console
     - **JDBC URL:** `jdbc:h2:mem:roombookingdb`
     - **User Name:** `sa`
     - **Password:** *(pozostaw puste)*

## Dokumentacja i materiały w repozytorium

- **[INSTRUKCJA.md](./INSTRUKCJA.md)** — przewodnik laboratoryjny krok po kroku dla studentów wraz z wyjaśnieniem Spring Data JPA, relacji `@ManyToOne`, Bean Validation (`@Valid`) oraz RFC 9457 `ProblemDetail`, zadaniami (Zadania 1–6 + zadanie dodatkowe), podpowiedziami i checklistą odbioru.
- **[room-booking-zjazd2-starter.md](./room-booking-zjazd2-starter.md)** — szczegółowa dokumentacja techniczna projektu startowego na Zjazd 2 (omówienie zależności w `pom.xml`, konfiguracji bazy H2 i Hibernate w `application.yml`, encji JPA, repozytoriów, wyjątków oraz pliku testowego `api-tests.http`).
- **[SCENARIUSZ_ZAJEC.md](./SCENARIUSZ_ZAJEC.md)** — konspekt i harmonogram prowadzenia 3-godzinnych (180 min) zajęć laboratoryjnych minuta po minucie (dla prowadzącego: plan bloków, live coding, checkpointy oraz najczęstsze problemy techniczne).

## Struktura projektu

```text
src/main/java/pl/lab/roombooking/
├── RoomBookingApplication.java          # Punkt startowy aplikacji Spring Boot
├── config/
│   ├── DataInitializer.java             # Inicjalizacja przykładowych sal i rezerwacji w bazie H2
│   └── OpenApiConfig.java               # Konfiguracja tytułu i opisu w Swagger UI
├── controller/
│   ├── HealthController.java            # Endpoint diagnostyczny GET /api/health
│   ├── RoomController.java              # Gotowy CRUD sal (JPA) + TODO rezerwacji sali
│   └── ReservationController.java       # Kontroler REST dla zasobu /api/reservations (TODO)
├── dto/
│   ├── CreateRoomRequest.java           # DTO żądania utworzenia sali (TODO: walidacja)
│   ├── UpdateRoomRequest.java           # DTO żądania aktualizacji sali (TODO: walidacja)
│   ├── RoomResponse.java                # DTO odpowiedzi sali
│   ├── CreateReservationRequest.java    # DTO żądania utworzenia rezerwacji (TODO: walidacja)
│   └── ReservationResponse.java         # DTO odpowiedzi rezerwacji
├── exception/
│   ├── ResourceNotFoundException.java   # Wyjątek biznesowy -> 404 Not Found
│   ├── BookingConflictException.java    # Wyjątek biznesowy -> 409 Conflict
│   └── GlobalExceptionHandler.java      # @RestControllerAdvice + ProblemDetail (TODO)
├── model/
│   ├── Room.java                        # Encja JPA sali (tabela 'rooms')
│   ├── Reservation.java                 # Encja JPA rezerwacji (tabela 'reservations', @ManyToOne)
│   └── ReservationStatus.java           # Enum statusu rezerwacji (CONFIRMED, CANCELLED)
├── repository/
│   ├── RoomRepository.java              # Repozytorium Spring Data JPA dla Room
│   └── ReservationRepository.java       # Repozytorium Spring Data JPA dla Reservation (TODO)
└── service/
    ├── RoomService.java                 # Logika biznesowa sal oparta na JPA
    └── ReservationService.java          # Logika rezerwacji, kolizji i transakcji (TODO)
```
