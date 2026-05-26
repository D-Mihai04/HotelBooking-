package controller;

import model.Hotel;
import model.Room;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final BookingService bookingService = new BookingService();


    @GetMapping
    public List<Hotel> getAllHotels() {
        return bookingService.getAllHotels();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable int id) {
        Hotel hotel = bookingService.getHotelById(id);
        return hotel != null ? ResponseEntity.ok(hotel) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/rooms")
    public List<Room> getRoomsByHotel(@PathVariable int id) {
        return bookingService.getRoomsByHotel(id);
    }


    @GetMapping("/{id}/rooms/available")
    public List<Room> getAvailableRooms(
            @PathVariable int id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return bookingService.getAvailableRooms(id, checkIn, checkOut);
    }


    @GetMapping("/search")
    public List<Hotel> searchHotels(@RequestParam String query) {
        return bookingService.searchHotels(query);
    }
}
