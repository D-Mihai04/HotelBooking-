package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = org.example.Main.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private int testUserId;

    @BeforeEach
    void registerTestUser() throws Exception {
        // Register a fresh user for each test (use timestamp to avoid duplicates)
        String email = "bookingtest_" + System.currentTimeMillis() + "@test.com";
        String body  = String.format("{\"name\":\"Booking Tester\",\"email\":\"%s\",\"password\":\"pass\"}", email);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        testUserId = json.get("id").asInt();
    }

    // ── Create Booking ────────────────────────────────────────

    @Test
    void createBooking_validData_returnsBooking() throws Exception {
        String body = String.format(
                "{\"userId\":\"%d\",\"roomId\":\"2\",\"checkIn\":\"2026-09-01\",\"checkOut\":\"2026-09-04\"}",
                testUserId);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingID").isNotEmpty());
    }

    @Test
    void createBooking_checkOutBeforeCheckIn_returns400() throws Exception {
        String body = String.format(
                "{\"userId\":\"%d\",\"roomId\":\"2\",\"checkIn\":\"2026-09-10\",\"checkOut\":\"2026-09-05\"}",
                testUserId);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_pastDate_returns400() throws Exception {
        String body = String.format(
                "{\"userId\":\"%d\",\"roomId\":\"2\",\"checkIn\":\"2020-01-01\",\"checkOut\":\"2020-01-05\"}",
                testUserId);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"1\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── Double Booking Prevention ─────────────────────────────

    @Test
    void createBooking_overlappingDates_returns400() throws Exception {
        // First booking on room 2
        String first = String.format(
                "{\"userId\":\"%d\",\"roomId\":\"2\",\"checkIn\":\"2026-10-01\",\"checkOut\":\"2026-10-05\"}",
                testUserId);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(first))
                .andExpect(status().isOk());

        // Second booking on the SAME room 2 with overlapping dates
        String second = String.format(
                "{\"userId\":\"%d\",\"roomId\":\"2\",\"checkIn\":\"2026-10-03\",\"checkOut\":\"2026-10-07\"}",
                testUserId);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(second))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── Get User Bookings ─────────────────────────────────────

    @Test
    void getUserBookings_validUser_returnsArray() throws Exception {
        mockMvc.perform(get("/api/bookings/user/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserBookings_afterBooking_containsBooking() throws Exception {
        // Make a booking
        String body = String.format(
                "{\"userId\":\"%d\",\"roomId\":\"4\",\"checkIn\":\"2026-11-01\",\"checkOut\":\"2026-11-03\"}",
                testUserId);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Verify it appears in list
        mockMvc.perform(get("/api/bookings/user/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }
}