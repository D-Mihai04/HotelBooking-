package controller;

import model.Booking;
import model.Hotel;
import model.Room;
import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookingService;
import repository.HotelRepository;
import repository.RoomRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookingService bookingService = new BookingService();
    private final HotelRepository hotelRepository = new HotelRepository();
    private final RoomRepository roomRepository = new RoomRepository();
    private final UserRepository userRepository = new UserRepository();

    //Rooms
    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<?> getRooms(@PathVariable int hotelId,
                                      @RequestParam int adminId) {
        if (!isAdmin(adminId, hotelId)) return forbidden();
        return ResponseEntity.ok(roomRepository.findByHotelId(hotelId));
    }


    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<?> addRoom(@PathVariable int hotelId,
                                     @RequestParam int adminId,
                                     @RequestBody Map<String, String> body) {
        if (!isAdmin(adminId, hotelId)) return forbidden();
        Room room = new Room(0, hotelId,
                body.get("roomNumber"),
                body.get("type"),
                Integer.parseInt(body.get("capacity")),
                Double.parseDouble(body.get("pricePerNight")),
                true);
        return ResponseEntity.ok(bookingService.createRoom(room));
    }


    @PutMapping("/hotels/{hotelId}/rooms/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable int hotelId,
                                        @PathVariable int roomId,
                                        @RequestParam int adminId,
                                        @RequestBody Map<String, String> body) {
        if (!isAdmin(adminId, hotelId)) return forbidden();
        Room room = new Room(roomId, hotelId,
                body.get("roomNumber"),
                body.get("type"),
                Integer.parseInt(body.get("capacity")),
                Double.parseDouble(body.get("pricePerNight")),
                Boolean.parseBoolean(body.get("available")));
        boolean ok = bookingService.updateRoom(room);
        return ok ? ResponseEntity.ok(Map.of("success", true))
                : ResponseEntity.badRequest().body(Map.of("error", "Update failed"));
    }


    @DeleteMapping("/hotels/{hotelId}/rooms/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable int hotelId,
                                        @PathVariable int roomId,
                                        @RequestParam int adminId) {
        if (!isAdmin(adminId, hotelId)) return forbidden();
        boolean ok = bookingService.deleteRoom(roomId, hotelId);
        return ok ? ResponseEntity.ok(Map.of("success", true))
                : ResponseEntity.badRequest().body(Map.of("error", "Delete failed"));
    }

    //Bookings
    @GetMapping("/hotels/{hotelId}/bookings")
    public ResponseEntity<?> getBookings(@PathVariable int hotelId,
                                         @RequestParam int adminId) {
        if (!isAdmin(adminId, hotelId)) return forbidden();

        List<Booking> bookings = bookingService.getBookingsByHotel(hotelId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Booking b : bookings) {
            Room room = bookingService.getRoomById(b.getRoomID());
            result.add(Map.of(
                    "bookingId",  b.getBookingID(),
                    "customerId", b.getCustomerID(),
                    "roomId",     b.getRoomID(),
                    "roomNumber", room != null ? room.getRoomNumber() : "—",
                    "roomType",   room != null ? room.getType() : "—",
                    "checkIn",    b.getCheckInDate().toString(),
                    "checkOut",   b.getCheckOutDate().toString(),
                    "nights",     b.getNights()
            ));
        }
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable int bookingId,
                                           @RequestParam int adminId,
                                           @RequestParam int hotelId) {
        if (!isAdmin(adminId, hotelId)) return forbidden();
        boolean ok = bookingService.cancelBooking(bookingId);
        return ok ? ResponseEntity.ok(Map.of("success", true))
                : ResponseEntity.badRequest().body(Map.of("error", "Cancel failed"));
    }

    private boolean isAdmin(int userId, int hotelId) {
        model.User user = userRepository.findById(userId);
        return user != null
                && "ADMIN".equals(user.getRole())
                && user.getHotelId() == hotelId;
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
    }
}