package service;

import model.Booking;
import model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = org.example.Main.class)
class BookingServiceTest {

    private final BookingService service = new BookingService();

    // ── makeBooking validations ────────────────────────────────

    @Test
    void makeBooking_checkOutBeforeCheckIn_returnsNull() {
        LocalDate checkIn  = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        Booking result = service.makeBooking(1, 1, checkIn, checkOut);
        assertNull(result, "Booking with inverted dates should return null");
    }

    @Test
    void makeBooking_sameDayCheckInOut_returnsNull() {
        LocalDate today = LocalDate.now().plusDays(3);

        Booking result = service.makeBooking(1, 1, today, today);
        assertNull(result, "Same-day check-in and check-out should return null");
    }

    @Test
    void makeBooking_pastCheckIn_returnsNull() {
        LocalDate checkIn  = LocalDate.now().minusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        Booking result = service.makeBooking(1, 1, checkIn, checkOut);
        assertNull(result, "Booking in the past should return null");
    }

    // ── registerUser validations ───────────────────────────────

    @Test
    void registerUser_duplicateEmail_returnsNull() {
        String email = "duptest_" + System.currentTimeMillis() + "@test.com";

        User first  = service.registerUser("First",  email, "pass1");
        User second = service.registerUser("Second", email, "pass2");

        assertNotNull(first,  "First registration should succeed");
        assertNull(second,    "Duplicate email registration should return null");
    }

    @Test
    void registerUser_newEmail_returnsUser() {
        String email = "newuser_" + System.currentTimeMillis() + "@test.com";

        User user = service.registerUser("New User", email, "password");

        assertNotNull(user);
        assertTrue(user.getId() > 0, "Saved user should have a DB-assigned ID");
        assertEquals(email, user.getEmail());
    }

    // ── loginUser validations ──────────────────────────────────

    @Test
    void loginUser_wrongPassword_returnsNull() {
        String email = "logintest_" + System.currentTimeMillis() + "@test.com";
        service.registerUser("Login Test", email, "correctpass");

        User result = service.loginUser(email, "wrongpass");
        assertNull(result, "Login with wrong password should return null");
    }

    @Test
    void loginUser_correctCredentials_returnsUser() {
        String email = "correct_" + System.currentTimeMillis() + "@test.com";
        service.registerUser("Correct User", email, "mypassword");

        User result = service.loginUser(email, "mypassword");
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void loginUser_nonexistentEmail_returnsNull() {
        User result = service.loginUser("nobody_" + System.currentTimeMillis() + "@nowhere.com", "pass");
        assertNull(result);
    }

    // ── getHotels / getRooms ───────────────────────────────────

    @Test
    void getAllHotels_returnsNonEmptyList() {
        assertFalse(service.getAllHotels().isEmpty(), "Hotel list should not be empty after seeding");
    }

    @Test
    void getRoomsByHotel_knownHotel_returnsRooms() {
        assertFalse(service.getRoomsByHotel(1).isEmpty(), "Hotel 1 should have rooms");
    }

    @Test
    void getAvailableRooms_futureDates_returnsRooms() {
        LocalDate checkIn  = LocalDate.now().plusDays(60);
        LocalDate checkOut = LocalDate.now().plusDays(65);

        // Far-future dates should have available rooms
        assertNotNull(service.getAvailableRooms(1, checkIn, checkOut));
    }
}