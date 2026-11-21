# Dokumentacja techniczna projektu startowego (`room-booking-zjazd2-starter`)

Niniejszy dokument opisuje budowę projektu startowego przygotowanego na **Zjazd 2** przedmiotu **Programowanie aplikacji webowych w Javie**. Projekt stanowi bezpośrednią kontynuację aplikacji ze Zjazdu 1, przenosząc warstwę sal na **Spring Data JPA** i dodając moduł **rezerwacji sal**, **walidację wejścia** oraz **centralną obsługę błędów**.

---

## 1. Nowe zależności i konfiguracja (`pom.xml` oraz `application.yml`)

### 1.1. Dodane startery w `pom.xml` (Spring Boot 4.1.3)
* **`spring-boot-starter-data-jpa`** — integruje Spring Data JPA z silnikiem **Hibernate ORM**, zarządzaniem transakcjami (`@Transactional`) oraz pulą połączeń HikariCP.
* **`spring-boot-starter-validation`** — dodaje specyfikację **Jakarta Bean Validation** wraz z implementacją Hibernate Validator do walidacji obiektów DTO adnotacją `@Valid`.
* **`spring-boot-h2console` oraz `com.h2database:h2`** — dostarcza wbudowaną relacyjną bazę danych H2 działającą w pamięci RAM oraz interfejs administracyjny dostępny w przeglądarce pod adresem `/h2-console`.
* **`spring-boot-starter-data-jpa-test`** — narzędzia do testowania warstwy persystencji.

### 1.2. Konfiguracja bazy danych w `src/main/resources/application.yml`
* `spring.datasource.url: jdbc:h2:mem:roombookingdb;DB_CLOSE_DELAY=-1` — baza danych w pamięci utrzymywana przez cały czas życia procesu JVM.
* `spring.h2.console.enabled: true` (`path: /h2-console`) — włącza webową konsolę SQL do podglądu tabel i wykonywania zapytań testowych.
* `spring.jpa.hibernate.ddl-auto: update` — Hibernate automatycznie tworzy i aktualizuje schemat tabel (`ROOMS`, `RESERVATIONS`) na podstawie adnotacji `@Entity`.
* `spring.jpa.show-sql: true` oraz `format_sql: true` — loguje czytelne zapytania SQL w konsoli aplikacji, co ułatwia studentom zrozumienie działania ORM.

---

## 2. Przegląd pakietów i nowych klas

### 2.1. Inicjalizacja danych (`pl.lab.roombooking.config.DataInitializer`)
Komponent implementujący `CommandLineRunner`. Uruchamia się automatycznie po starcie kontekstu Springa i (jeśli tabela `rooms` jest pusta) zapisuje **3 sale** (`Sala A101`, `Aula B204`, `Laboratorium C012`) oraz **2 przykładowe rezerwacje** na kolejny dzień.

### 2.2. Encje JPA i relacje (`pl.lab.roombooking.model.*`)
* **`Room`** — encja zmapowana na tabelę `rooms` (`@Entity`, `@Table(name = "rooms")`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`).
* **`Reservation`** — encja zmapowana na tabelę `reservations`. Zawiera klucz obcy `room_id` skonfigurowany relacją **`@ManyToOne(fetch = FetchType.LAZY, optional = false)`** z encją `Room`, zakres czasowy (`startTime`, `endTime` jako `LocalDateTime`), dane rezerwującego oraz pole `status` (`@Enumerated(EnumType.STRING)`).
* **`ReservationStatus`** — typ wyliczeniowy o wartościach `CONFIRMED` oraz `CANCELLED`.

### 2.3. Repozytoria Spring Data JPA (`pl.lab.roombooking.repository.*`)
* **`RoomRepository`** — interfejs rozszerzający `JpaRepository<Room, Long>`, zapewniający pełny zestaw operacji bazodanowych bez pisania kodu SQL.
* **`ReservationRepository`** — interfejs rozszerzający `JpaRepository<Reservation, Long>`, zawierający miejsca `TODO` na:
  * metody pochodne od nazw (`findByRoomIdOrderByStartTimeAsc`, `findByStatusOrderByStartTimeAsc`),
  * niestandardowe zapytanie JPQL z adnotacją `@Query` (`existsOverlappingReservation`) wykrywające kolizje przedziałów czasowych.

### 2.4. Wyjątki biznesowe i obsługa błędów (`pl.lab.roombooking.exception.*`)
* **`ResourceNotFoundException`** — rzucany, gdy sala lub rezerwacja o wskazanym ID nie istnieje (`404 Not Found`).
* **`BookingConflictException`** — rzucany przy konflikcie terminów rezerwacji, próbie rezerwacji niedostępnej sali lub ponownym anulowaniu (`409 Conflict`).
* **`GlobalExceptionHandler`** — klasa `@RestControllerAdvice` przechwytująca wyjątki z kontrolerów i budująca ustrukturyzowane obiekty **`ProblemDetail`** (standard RFC 9457). Zawiera gotową obsługę `ResourceNotFoundException` oraz miejsca `TODO` dla `BookingConflictException`, `IllegalArgumentException` i `MethodArgumentNotValidException`.

### 2.5. Warstwa serwisów i kontrolerów
* **`RoomService` oraz `RoomController`** — zawierają już działający, kompletny CRUD dla sal oparty na `RoomRepository` (stan docelowy po Zjeździe 1 przeniesiony na bazę danych), a także miejsca `TODO` na dopisanie `@Valid` (Zadanie 5) oraz endpointu `GET /api/rooms/{roomId}/reservations` (Zadanie 1).
* **`ReservationService` oraz `ReservationController`** — szkielet modułu rezerwacji oznaczony adnotacją `@Transactional(readOnly = true)` na poziomie klasy oraz `@Transactional` na metodach modyfikujących dane, z zadaniami `TODO` (Zadania 1–5).

---

## 3. Plik testowy `api-tests.http`

Plik `api-tests.http` w katalogu głównym zawiera 12 scenariuszy testowych pokrywających wszystkie wymagania Zjazdu 2: pobranie danych z bazy H2, odczyt rezerwacji sali, tworzenie rezerwacji (`201 Created`), wykrywanie konfliktu terminów (`409 Conflict`), blokadę niedostępnej sali (`409 Conflict`), błędy walidacji Bean Validation (`400 Bad Request` z polem `errors`), anulowanie rezerwacji (`PATCH .../cancel`) oraz filtrowanie po statusie.
