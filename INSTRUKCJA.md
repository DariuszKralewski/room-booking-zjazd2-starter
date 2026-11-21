# Przewodnik laboratoryjny: Bazy danych (JPA), walidacja i obsługa wyjątków (Zjazd 2)

## 1. Cel zajęć

Na dzisiejszych laboratoriach przekształcisz aplikację **Room Booking Service** z prototypu działającego w pamięci RAM w pełnoprawny system oparty na **relacyjnej bazie danych**, wzbogacony o moduł **rezerwacji sal**, **deklaratywną walidację wejścia** oraz **ustandaryzowaną obsługę błędów HTTP**.

Po ukończeniu ćwiczeń będziesz potrafić:
1. Mapować klasy Javy na tabele relacyjne z wykorzystaniem **Spring Data JPA / Hibernate** (`@Entity`, `@Id`, `@GeneratedValue`, `@ManyToOne`).
2. Tworzyć zapytania bazodanowe w interfejsach `JpaRepository` za pomocą **metod pochodnych od nazw** oraz adnotacji **`@Query` (JPQL)**.
3. Świadomie zarządzać granicami transakcji przy użyciu adnotacji **`@Transactional`**.
4. Walidować dane wejściowe w obiektach DTO za pomocą **Bean Validation** (`@Valid`, `@NotBlank`, `@Email`, `@Future`, `@Positive`).
5. Zastąpić domyślne błędy serwera spójnymi odpowiedziami JSON zgodnymi ze standardem **RFC 9457 (`ProblemDetail`)** przy użyciu **`@RestControllerAdvice`**.

---

## 2. Co zmieniło się względem Zjazdu 1?

### 2.1. Od `ConcurrentHashMap` do Spring Data JPA
Na Zjeździe 1 wszystkie dane trzymaliśmy w mapie w pamięci RAM (`InMemoryRoomRepository`). Dzięki temu, że od początku stosowaliśmy architekturę warstwową i wstrzykiwanie zależności (DI), migracja warstwy sal na trwałą bazę danych wymagała jedynie:
- Dodania adnotacji `@Entity` i `@Table(name = "rooms")` w klasie `Room`.
- Zastąpienia klasy repozytorium jedno-linijkowym interfejsem:
  ```java
  public interface RoomRepository extends JpaRepository<Room, Long> {
  }
  ```
- Spring Data JPA w czasie uruchamiania aplikacji sam generuje implementację tego interfejsu, tłumacząc wywołania metod (`findAll()`, `findById()`, `save()`, `deleteById()`) na zapytania SQL wykonywane przez Hibernate.

### 2.2. Relacja `@ManyToOne` między rezerwacją a salą
W bazie danych pojawiła się druga tabela — `reservations` (encja `Reservation`). Jedna sala może mieć wiele rezerwacji w różnych terminach, natomiast każda rezerwacja dotyczy dokładnie jednej sali:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "room_id", nullable = false)
private Room room;
```

* **`@JoinColumn(name = "room_id")`** tworzy w tabeli `reservations` kolumnę klucza obcego wskazującą na `rooms.id`.
* **`fetch = FetchType.LAZY`** oznacza **leniwe ładowanie**: Hibernate pobiera z bazy wiersz powiązanej sali dopiero w momencie wywołania `reservation.getRoom()`. Aby odczyt relacji `LAZY` zadziałał poprawnie podczas budowania `ReservationResponse`, metoda serwisu musi działać wewnątrz aktywnej sesji bazodanowej (`@Transactional`).

### 2.3. Centralna obsługa wyjątków (`@RestControllerAdvice` + `ProblemDetail`)
Zamiast zwracać w każdym kontrolerze ręcznie `ResponseEntity.notFound().build()` lub `ResponseEntity.badRequest().build()`, w profesjonalnych API stosuje się **wyjątki domenowe** (`ResourceNotFoundException`, `BookingConflictException`), które są przechwytywane w jednym miejscu — w klasie oznaczonej adnotacją `@RestControllerAdvice` (`GlobalExceptionHandler`).

Spring Boot wspiera natywnie standard **RFC 9457 (`ProblemDetail`)**, zwracając czytelny JSON błędu:
```json
{
  "type": "about:blank",
  "title": "Konflikt rezerwacji",
  "status": 409,
  "detail": "Sala jest już zarezerwowana w wybranym przedziale czasowym.",
  "instance": "/api/reservations",
  "timestamp": "2026-11-21T09:15:30.123Z"
}
```

---

## 3. Uruchomienie projektu i podgląd bazy danych w H2 Console

### Krok 1: Uruchom aplikację
1. Otwórz folder `room-booking-zjazd2-starter` w IDE i uruchom `RoomBookingApplication` (lub `mvn spring-boot:run`).
2. Przy starcie klasa `DataInitializer` automatycznie zapisze do bazy **3 przykładowe sale** oraz **2 rezerwacje** na jutrzejszy dzień.
3. W konsoli IDE zobaczysz wygenerowane przez Hibernate zapytania SQL (`spring.jpa.show-sql: true`).

### Krok 2: Zaloguj się do H2 Console
1. Otwórz w przeglądarce adres: **`http://localhost:8080/h2-console`**
2. Wpisz dokładnie poniższe dane logowania i kliknij **Connect**:
   - **JDBC URL:** `jdbc:h2:mem:roombookingdb`
   - **User Name:** `sa`
   - **Password:** *(zostaw puste)*
3. Po lewej stronie zobaczysz tabele `ROOMS` oraz `RESERVATIONS`. Możesz wykonać `SELECT * FROM RESERVATIONS;` i podejrzeć kolumnę klucza obcego `ROOM_ID`.

---

## 4. Zadania laboratoryjne do wykonania

Miejsca do uzupełnienia zostały oznaczone komentarzami `// TODO (Zadanie X)`. Realizuj zadania po kolei, weryfikując efekty w **Swagger UI** (`http://localhost:8080/swagger-ui.html`), w pliku **`api-tests.http`** oraz w konsoli bazy danych **`/h2-console`**.

---

### Zadanie 1: Pobieranie rezerwacji dla wskazanej sali (`GET /api/rooms/{roomId}/reservations`)

**Cel:** Wykorzystać metodę pochodną od nazwy w Spring Data JPA oraz połączyć zasoby relacją nadrzędny → podrzędny.

**Wymagania:**
1. W interfejsie `ReservationRepository` odkomentuj / zadeklaruj metodę pochodną:
   ```java
   List<Reservation> findByRoomIdOrderByStartTimeAsc(Long roomId);
   ```
   *(Spring Data automatycznie przetłumaczy nazwę tej metody na zapytanie `SELECT * FROM reservations WHERE room_id = ? ORDER BY start_time ASC`.)*
2. W `ReservationService` zaimplementuj `getReservationsForRoom(Long roomId)`:
   - Najpierw sprawdź, czy sala istnieje: `if (!roomRepository.existsById(roomId))` → rzuć `new ResourceNotFoundException("Nie znaleziono sali o ID: " + roomId)`.
   - Pobierz rezerwacje z repozytorium i zmapuj je na `List<ReservationResponse>`.
3. W `RoomController` dodaj endpoint `@GetMapping("/{roomId}/reservations")`, który wywołuje `reservationService.getReservationsForRoom(roomId)` i zwraca `200 OK`.

**Weryfikacja:**
- `GET http://localhost:8080/api/rooms/1/reservations` → `200 OK` (lista z rezerwacją Jana Kowalskiego).
- `GET http://localhost:8080/api/rooms/999/reservations` → `404 Not Found` z JSON-em `ProblemDetail`.

---

### Zadanie 2: Tworzenie rezerwacji z wykrywaniem kolizji terminów (`POST /api/reservations`)

**Cel:** Zaimplementować zapis nowej rezerwacji w transakcji wraz z regułami biznesowymi chroniącymi przed podwójną rezerwacją tej samej sali.

**Wymagania:**
1. W `ReservationRepository` odkomentuj metodę `existsOverlappingReservation(...)` z adnotacją `@Query`. Zwróć uwagę na warunek nakładania się przedziałów czasowych:
   `r.startTime < :endTime AND r.endTime > :startTime` (uwzględniamy wyłącznie rezerwacje o statusie `CONFIRMED`).
2. W `ReservationService` zaimplementuj metodę `createReservation(CreateReservationRequest request)` (oznaczoną `@Transactional`):
   - **Reguła 1 (kolejność dat):** Jeśli `!request.startTime().isBefore(request.endTime())` → rzuć `IllegalArgumentException("Czas rozpoczęcia musi być wcześniejszy niż czas zakończenia")`.
   - **Reguła 2 (istnienie sali):** Pobierz encję `Room` przez `roomRepository.findById(request.roomId())` lub rzuć `ResourceNotFoundException`.
   - **Reguła 3 (dostępność sali):** Jeśli `!room.isAvailable()` → rzuć `BookingConflictException("Sala '" + room.getName() + "' jest obecnie wyłączona z rezerwacji")`.
   - **Reguła 4 (brak kolizji):** Jeśli `reservationRepository.existsOverlappingReservation(room.getId(), request.startTime(), request.endTime())` zwróci `true` → rzuć `BookingConflictException("Sala jest już zarezerwowana w wybranym przedziale czasowym")`.
   - **Zapis:** Utwórz nowy obiekt `Reservation`, zapisz go przez `reservationRepository.save(...)` i zwróć `ReservationResponse.from(saved)`.
3. W `ReservationController` dodaj endpoint `@PostMapping` zwracający **`201 Created`** z nagłówkiem `Location: /api/reservations/{id}` oraz utworzonym `ReservationResponse`.

**Weryfikacja:**
- Wyślij żądanie nr 5 z `api-tests.http` → `201 Created` (sprawdź nowy wiersz w `/h2-console`).
- Wyślij żądanie nr 6 (nakładające się godziny dla tej samej sali) → `BookingConflictException`.
- Wyślij żądanie nr 7 (rezerwacja sali nr 3, która ma `available = false`) → `BookingConflictException`.

<details>
<summary>Podpowiedź (rozwiń w razie trudności)</summary>

W `ReservationService`:
```java
@Transactional
public ReservationResponse createReservation(CreateReservationRequest request) {
    if (!request.startTime().isBefore(request.endTime())) {
        throw new IllegalArgumentException("Czas rozpoczęcia rezerwacji musi być wcześniejszy niż czas zakończenia.");
    }

    Room room = roomRepository.findById(request.roomId())
            .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono sali o ID: " + request.roomId()));

    if (!room.isAvailable()) {
        throw new BookingConflictException("Sala '" + room.getName() + "' jest obecnie wyłączona z rezerwacji.");
    }

    boolean conflict = reservationRepository.existsOverlappingReservation(
            room.getId(),
            request.startTime(),
            request.endTime()
    );
    if (conflict) {
        throw new BookingConflictException("Sala '" + room.getName() + "' jest już zarezerwowana w podanym terminie.");
    }

    Reservation reservation = new Reservation(
            room,
            request.reservedBy(),
            request.contactEmail(),
            request.purpose(),
            request.startTime(),
            request.endTime()
    );

    Reservation saved = reservationRepository.save(reservation);
    return ReservationResponse.from(saved);
}
```
</details>

---

### Zadanie 3: Pobieranie szczegółów i anulowanie rezerwacji (`GET /api/reservations/{id}` oraz `PATCH /api/reservations/{id}/cancel`)

**Cel:** Zaimplementować zmianę stanu encji zarządzanej przez Hibernate (*dirty checking*) bez fizycznego usuwania rekordu z bazy danych.

**Wymagania:**
1. W `ReservationService` zaimplementuj `getReservationById(Long id)`:
   - Wyszukaj rezerwację lub rzuć `ResourceNotFoundException("Nie znaleziono rezerwacji o ID: " + id)`.
2. W `ReservationService` zaimplementuj `cancelReservation(Long id)` (oznaczoną `@Transactional`):
   - Pobierz rezerwację lub rzuć `ResourceNotFoundException`.
   - Jeśli `reservation.getStatus() == ReservationStatus.CANCELLED` → rzuć `BookingConflictException("Rezerwacja o ID " + id + " została już wcześniej anulowana.")`.
   - Ustaw `reservation.setStatus(ReservationStatus.CANCELLED)` — zauważ, że wewnątrz metody `@Transactional` Hibernate automatycznie wykryje zmianę pola encji (*dirty checking*) i wyśle `UPDATE reservations SET status = 'CANCELLED' WHERE id = ?` przy zatwierdzaniu transakcji!
3. W `ReservationController` dodaj oba endpointy:
   - `@GetMapping("/{id}")` → zwraca `200 OK` i `ReservationResponse`.
   - `@PatchMapping("/{id}/cancel")` → zwraca `200 OK` i zaktualizowany `ReservationResponse`.

**Weryfikacja:**
- Wywołaj `PATCH http://localhost:8080/api/reservations/1/cancel` → `200 OK`, `"status": "CANCELLED"`.
- Ponowne wywołanie tego samego żądania → `BookingConflictException`.
- Spróbuj teraz zarezerwować salę nr 1 w terminie anulowanej rezerwacji — powinno się udać, ponieważ zapytanie z Zadania 2 sprawdza tylko status `CONFIRMED`!

---

### Zadanie 4: Filtrowanie rezerwacji po statusie (`GET /api/reservations?status=CONFIRMED`)

**Cel:** Wykorzystać typ wyliczeniowy (`Enum`) jako parametr zapytania HTTP i metodę pochodną w repozytorium.

**Wymagania:**
1. W `ReservationRepository` dodaj metodę:
   ```java
   List<Reservation> findByStatusOrderByStartTimeAsc(ReservationStatus status);
   ```
2. W `ReservationService.getAllReservations(ReservationStatus status)`:
   - Jeśli `status != null`, pobierz wyniki przez `reservationRepository.findByStatusOrderByStartTimeAsc(status)`.
   - Jeśli `status == null`, pobierz wszystkie rezerwacje posortowane po `startTime`.

**Weryfikacja:**
- `GET http://localhost:8080/api/reservations?status=CANCELLED` zwraca wyłącznie anulowane rezerwacje.

---

### Zadanie 5: Deklaratywna walidacja danych wejściowych (`@Valid` i Bean Validation)

**Cel:** Zastąpić ręczne instrukcje `if` adnotacjami standardu **Jakarta Bean Validation**, które uruchamiają się automatycznie przed wejściem do metody kontrolera.

**Wymagania:**
1. W rekordzie **`CreateReservationRequest`** dodaj adnotacje walidacyjne:
   - `roomId`: `@NotNull(message = "Identyfikator sali jest wymagany")`
   - `reservedBy`: `@NotBlank(message = "Imię i nazwisko rezerwującego jest wymagane") @Size(min = 3, max = 120, message = "Dane rezerwującego muszą mieć od 3 do 120 znaków")`
   - `contactEmail`: `@NotBlank(message = "Adres e-mail jest wymagany") @Email(message = "Niepoprawny format adresu e-mail")`
   - `purpose`: `@NotBlank(message = "Cel rezerwacji jest wymagany") @Size(min = 3, max = 250, message = "Cel rezerwacji musi mieć od 3 do 250 znaków")`
   - `startTime`: `@NotNull(message = "Czas rozpoczęcia jest wymagany") @Future(message = "Czas rozpoczęcia musi być w przyszłości")`
   - `endTime`: `@NotNull(message = "Czas zakończenia jest wymagany") @Future(message = "Czas zakończenia musi być w przyszłości")`
2. W rekordach **`CreateRoomRequest`** oraz **`UpdateRoomRequest`** dodaj:
   - `name`: `@NotBlank(message = "Nazwa sali jest wymagana") @Size(max = 100)`
   - `building`: `@NotBlank(message = "Nazwa budynku jest wymagana") @Size(max = 100)`
   - `floor`: `@Min(value = -2, message = "Piętro nie może być niższe niż -2")`
   - `capacity`: `@Positive(message = "Pojemność sali musi być większa od zera")`
3. W **`RoomController`** (`createRoom`, `updateRoom`) oraz **`ReservationController`** (`createReservation`) dodaj adnotację **`@Valid`** przed `@RequestBody`.

---

### Zadanie 6: Globalna obsługa wyjątków z użyciem `ProblemDetail` (`@RestControllerAdvice`)

**Cel:** Zamienić surowe błędy serwera na ustandaryzowane odpowiedzi HTTP `400 Bad Request` oraz `409 Conflict` w formacie RFC 9457 (`ProblemDetail`).

**Wymagania:**
W klasie `GlobalExceptionHandler` dopisz trzy metody z adnotacją `@ExceptionHandler`:
1. **`BookingConflictException`** → zwraca `ProblemDetail` ze statusem **`HttpStatus.CONFLICT` (`409`)** oraz tytułem `"Konflikt rezerwacji"`.
2. **`IllegalArgumentException`** → zwraca `ProblemDetail` ze statusem **`HttpStatus.BAD_REQUEST` (`400`)** oraz tytułem `"Niepoprawne argumenty żądania"`.
3. **`MethodArgumentNotValidException`** → zwraca `ProblemDetail` ze statusem **`HttpStatus.BAD_REQUEST` (`400`)**, tytułem `"Błąd walidacji danych wejściowych"` oraz dodatkową właściwością `"errors"`, zawierającą mapę `Map<String, String>` (`nazwaPola -> komunikatBłędu`).

<details>
<summary>Podpowiedź (rozwiń w razie trudności)</summary>

```java
@ExceptionHandler(BookingConflictException.class)
public ProblemDetail handleConflict(BookingConflictException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Konflikt rezerwacji");
    problem.setProperty("timestamp", Instant.now());
    return problem;
}

@ExceptionHandler(IllegalArgumentException.class)
public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Niepoprawne argumenty żądania");
    problem.setProperty("timestamp", Instant.now());
    return problem;
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Przesłane dane zawierają błędy walidacji."
    );
    problem.setTitle("Błąd walidacji danych wejściowych");

    Map<String, String> fieldErrors = new LinkedHashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
        fieldErrors.put(error.getField(), error.getDefaultMessage());
    }
    problem.setProperty("errors", fieldErrors);
    problem.setProperty("timestamp", Instant.now());
    return problem;
}
```
</details>

---

### Zadanie dodatkowe (dla chętnych): Raport liczby aktywnych rezerwacji dla każdej sali

1. Dodaj w `ReservationRepository` zapytanie agregujące JPQL (`SELECT r.room.id, r.room.name, COUNT(r) FROM Reservation r WHERE r.status = 'CONFIRMED' GROUP BY r.room.id, r.room.name`).
2. Utwórz rekord DTO `RoomUsageStatResponse(Long roomId, String roomName, long confirmedReservationsCount)` i wystaw endpoint `GET /api/rooms/stats`.

---

## 5. Checklista przed oddaniem zadania

Upewnij się, że Twoje rozwiązanie spełnia wszystkie poniższe punkty:
- [ ] Dane sal i rezerwacji są widoczne w tabelach `ROOMS` i `RESERVATIONS` w konsoli `/h2-console`.
- [ ] `GET /api/rooms/{roomId}/reservations` zwraca listę rezerwacji danej sali (`200 OK`) lub `404 Not Found` (`ProblemDetail`).
- [ ] `POST /api/reservations` zapisuje nową rezerwację (`201 Created` + nagłówek `Location`), blokuje terminy nakładające się (`409 Conflict`) oraz blokuje sale z `available = false` (`409 Conflict`).
- [ ] `PATCH /api/reservations/{id}/cancel` zmienia status rezerwacji na `CANCELLED` (`200 OK`) i zwalnia termin dla kolejnych rezerwacji, a przy ponownym wywołaniu zwraca `409 Conflict`.
- [ ] Przesłanie niepoprawnych danych w `POST /api/rooms`, `PUT /api/rooms/{id}` lub `POST /api/reservations` zwraca `400 Bad Request` z obiektem `ProblemDetail` zawierającym pole `errors`.
