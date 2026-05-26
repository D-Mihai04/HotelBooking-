package controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = org.example.Main.class)
@AutoConfigureMockMvc
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ── Hotels ────────────────────────────────────────────────

    @Test
    void getAllHotels_returnsNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getHotelById_validId_returnsHotel() throws Exception {
        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    void getHotelById_invalidId_returns404() throws Exception {
        mockMvc.perform(get("/api/hotels/99999"))
                .andExpect(status().isNotFound());
    }

    // ── Rooms ─────────────────────────────────────────────────

    @Test
    void getRoomsByHotel_returnsRooms() throws Exception {
        mockMvc.perform(get("/api/hotels/1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAvailableRooms_validDates_returnsArray() throws Exception {
        mockMvc.perform(get("/api/hotels/1/rooms/available")
                        .param("checkIn", "2026-08-01")
                        .param("checkOut", "2026-08-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAvailableRooms_missingParams_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels/1/rooms/available"))
                .andExpect(status().isBadRequest());
    }
}