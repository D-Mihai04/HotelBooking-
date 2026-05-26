package controller;

import model.Booking;
import model.Room;
import model.Hotel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookingService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService = new BookingService();

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, String> body) {
        try {
            int userId    = Integer.parseInt(body.get("userId"));
            int roomId    = Integer.parseInt(body.get("roomId"));
            LocalDate checkIn  = LocalDate.parse(body.get("checkIn"));
            LocalDate checkOut = LocalDate.parse(body.get("checkOut"));

            Booking booking = bookingService.makeBooking(userId, roomId, checkIn, checkOut);

            if (booking == null || booking.getBookingID() == 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Booking failed. Room may not be available."));
            }

            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable int userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);


        List<Map<String, Object>> result = new ArrayList<>();
        for (Booking b : bookings) {
            Room room = bookingService.getRoomById(b.getRoomID());
            Hotel hotel = room != null ? bookingService.getHotelById(room.getHotelId()) : null;

            result.add(Map.of(
                "bookingId",   b.getBookingID(),
                "roomId",      b.getRoomID(),
                "roomNumber",  room != null ? room.getRoomNumber() : "—",
                "roomType",    room != null ? room.getType() : "—",
                "hotelName",   hotel != null ? hotel.getName() : "—",
                "checkIn",     b.getCheckInDate().toString(),
                "checkOut",    b.getCheckOutDate().toString(),
                "nights",      b.getNights(),
                "totalPrice",  room != null ? b.getNights() * room.getPricePerNight() : 0
            ));
        }

        return ResponseEntity.ok(result);
    }
}
