# Scenariusz prowadzenia zajęć laboratoryjnych — Zjazd 2 (3h / 180 min)

**Przedmiot:** Programowanie aplikacji webowych w Javie  
**Temat:** Persystencja danych (Spring Data JPA), relacje encji, Bean Validation i obsługa wyjątków (`ProblemDetail`)  
**Repozytorium startowe:** `room-booking-zjazd2-starter`  

---

## Przygotowanie przed zajęciami (5 minut)

1. Rozpakuj archiwum `room-booking-zjazd2-starter` i wypchnij jego zawartość do repozytorium na GitHubie (jako nowe repozytorium lub osobny branch `zjazd-2-starter`).
2. Udostępnij studentom link na tablicy lub platformie zajęciowej.
3. Pełny przewodnik dla studentów wraz z teorią, zadaniami i podpowiedziami znajduje się wewnątrz repozytorium w pliku **`INSTRUKCJA.md`**.

---

## Harmonogram spotkania (180 minut)

### Blok 1: Rozruch, konsola H2 i przegląd migracji na JPA (0:00 – 0:30 | 30 min)

* **0:00–0:15 | Klonowanie, start aplikacji i logowanie do `/h2-console` (Studenci + Prowadzący):**
  * Studenci klonują repozytorium, uruchamiają `RoomBookingApplication` i otwierają w przeglądarce:
    * `http://localhost:8080/swagger-ui.html`
    * `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:roombookingdb`, User: `sa`, bez hasła).
  * **Kamień milowy:** każdy student widzi w konsoli H2 tabele `ROOMS` (3 wiersze) oraz `RESERVATIONS` (2 wiersze) utworzone przez Hibernate i `DataInitializer`.
* **0:15–0:30 | Omówienie architektury JPA na żywym kodzie (Ekran prowadzącego):**
  * Pokaż w `pom.xml` i `application.yml`, jak Spring Boot konfiguruje bazę danych, `ddl-auto: update` i logowanie SQL (`show-sql: true`).
  * Porównaj `InMemoryRoomRepository` ze Zjazdu 1 z interfejsem `RoomRepository extends JpaRepository<Room, Long>` — pokaż, że `RoomController` pozostał praktycznie niezmieniony.
  * Otwórz encję `Reservation.java` i wyjaśnij adnotacje `@Entity`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`, `@ManyToOne(fetch = FetchType.LAZY)` oraz `@JoinColumn(name = "room_id")`.

---

### Blok 2: Live Coding — wspólne wykonanie Zadania 1 (0:30 – 0:55 | 25 min)

Wykonaj **Zadanie 1 (`GET /api/rooms/{roomId}/reservations`)** wspólnie ze studentami na rzutniku:
1. Otwórz `ReservationRepository` i odkomentuj metodę `findByRoomIdOrderByStartTimeAsc(Long roomId)` — wyjaśnij mechanizm *Derived Query Methods* w Spring Data JPA.
2. Przejdź do `ReservationService` i napisz `getReservationsForRoom(Long roomId)` — pokaż sprawdzenie `roomRepository.existsById(roomId)` i rzucenie `ResourceNotFoundException`.
3. Zwróć uwagę na `@Transactional(readOnly = true)` nad klasą `ReservationService`: wyjaśnij, dlaczego dzięki temu wywołanie `reservation.getRoom().getName()` wewnątrz `ReservationResponse.from(...)` działa bez `LazyInitializationException`.
4. Dopisz `@GetMapping("/{roomId}/reservations")` w `RoomController` i przetestuj na żywo żądania nr 3 (`200 OK`) oraz nr 4 (`404 Not Found` z gotowym `ProblemDetail` z `GlobalExceptionHandler`) z pliku `api-tests.http`.

---

### Blok 3: Praca własna studentów — logika rezerwacji, JPQL `@Query` i transakcje: Zadania 2, 3 i 4 (0:55 – 1:55 | 60 min)

* Studenci realizują kolejno **Zadanie 2 (`POST /api/reservations` + wykrywanie kolizji)**, **Zadanie 3 (`GET /{id}` oraz `PATCH /{id}/cancel`)** i **Zadanie 4 (filtrowanie po statusie)**.
* **Na co zwracać uwagę podczas obchodu po sali:**
  * **W Zadaniu 2 (`POST`):**
    * Upewnij się, że studenci rozumieją warunek nakładania się przedziałów czasowych w `@Query` (`r.startTime < :endTime AND r.endTime > :startTime`) oraz sprawdzają flagę `room.isAvailable()`.
    * Sprawdź, czy endpoint zwraca **`201 Created` z nagłówkiem `Location`**.
  * **W Zadaniu 3 (`PATCH .../cancel`):**
    * Zwróć uwagę na mechanizm *dirty checking* w Hibernate wewnątrz metody `@Transactional` — wystarczy wywołać `reservation.setStatus(ReservationStatus.CANCELLED)`, aby przy zakończeniu transakcji Hibernate wysłał `UPDATE` do bazy.
    * Pokaż studentom w logach konsoli zapytanie `update reservations set ... where id=?`.
* **Checkpoint (~1:30):** Jeśli kilka osób ma problem z formatem daty JSON w `LocalDateTime`, pokaż poprawny format ISO-8601 (`"2027-05-10T10:00:00"`) z pliku `api-tests.http`.

---

### Blok 4: Mini-wykład + Bean Validation (`@Valid`) i `ProblemDetail`: Zadania 5 i 6 (1:55 – 2:35 | 40 min)

* **1:55–2:05 | Krótkie wprowadzenie (Ekran prowadzącego):**
  * Uruchom żądanie nr 6 lub 8 z `api-tests.http` *przed* wykonaniem Zadania 6 — pokaż studentom, że nieobsłużony `BookingConflictException` lub błąd walidacji zwraca domyślnie brzydki `500 Internal Server Error` ze śladem stosu.
  * Omów działanie Bean Validation (`@NotBlank`, `@Email`, `@Future`, `@Positive` + `@Valid` w kontrolerze) oraz klasę `GlobalExceptionHandler` (`@RestControllerAdvice` + RFC 9457 `ProblemDetail`).
* **2:05–2:35 | Praca studentów nad Zadaniami 5 i 6:**
  * Studenci dodają adnotacje walidacyjne w rekordach DTO oraz `@Valid` w kontrolerach (**Zadanie 5**), a następnie uzupełniają obsługę `BookingConflictException`, `IllegalArgumentException` i `MethodArgumentNotValidException` w `GlobalExceptionHandler` (**Zadanie 6**).
  * Osoby, które skończą wcześniej, realizują **Zadanie dodatkowe** (`GET /api/rooms/stats` z zapytaniem `GROUP BY`).

---

### Blok 5: Odbiór rozwiązań i zapowiedź Zjazdu 3 (2:35 – 3:00 | 25 min)

* **2:35–2:55 | Szybki odbiór z plikiem `api-tests.http` i `/h2-console`:**
  * Podejdź do każdego studenta i poproś o uruchomienie scenariuszy 3–11 z pliku `api-tests.http` według **Checklisty** z końca `INSTRUKCJA.md`, a następnie pokaż wiersze zapisane w tabeli `RESERVATIONS` w `/h2-console`.
* **2:55–3:00 | Podsumowanie i pomost do Zjazdu 3:**
  * Podkreśl, że aplikacja ma już kompletną logikę biznesową, trwałą bazę danych i spójne błędy API.
  * Zapowiedz, że na **Zjeździe 3** zabezpieczycie aplikację przy użyciu **Spring Security** (role `USER` i `ADMIN` — np. tylko `ADMIN` może dodawać/usuwać sale, a zalogowany `USER` może tworzyć i anulować rezerwacje), napiszecie **testy automatyczne (`MockMvc` + `@SpringBootTest`)** oraz podepniecie **prosty klient webowy (HTML + JS `fetch` / CORS)**, aby zobaczyć pełną komunikację przeglądarka ↔ backend.

---

## Najczęstsze problemy techniczne u studentów (ściągawka)

1. **Brak efektu adnotacji `@NotBlank` / `@Future` na rekordach DTO**  
   Student dopisał adnotacje w `CreateReservationRequest`, ale zapomniał dodać `@Valid` przed `@RequestBody` w `ReservationController` (lub `RoomController`).
2. **Błąd logowania do `/h2-console` (`Database "mem:testdb" not found`)**  
   Domyślnie formularz H2 podpowiada czasem `jdbc:h2:mem:testdb`. Należy wpisać dokładnie URL z `application.yml`: `jdbc:h2:mem:roombookingdb`.
3. **`LazyInitializationException` przy własnych metodach w serwisie**  
   Jeśli student dopisze nową metodę w klasie bez `@Transactional` (lub usunie `@Transactional(readOnly = true)` z góry `ReservationService`), odczyt `reservation.getRoom().getName()` poza sesją Hibernate rzuci wyjątek.
