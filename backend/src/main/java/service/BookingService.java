package service;

import model.Booking;
import model.Hotel;
import model.Room;
import model.User;
import org.example.Hasher;
import org.springframework.stereotype.Service;
import repository.BookingRepository;
import repository.HotelRepository;
import repository.RoomRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookingService() {
        this.hotelRepository = new HotelRepository();
        this.roomRepository = new RoomRepository();
        this.userRepository = new UserRepository();
        this.bookingRepository = new BookingRepository();
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(int id) {
        return hotelRepository.findById(id);
    }

    public List<Room> getAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(hotelId, checkIn, checkOut);
    }

    public List<Room> getRoomsByHotel(int hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(int roomId) {
        return roomRepository.findById(roomId);
    }

    public Booking makeBooking(int userId, int roomId, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) return null;
        if (checkIn.isBefore(LocalDate.now())) return null;
        if (!roomRepository.isRoomAvailable(roomId, checkIn, checkOut)) return null;

        Booking booking = new Booking(0, userId, roomId, checkIn, checkOut);
        return bookingRepository.save(booking);
    }

    public User registerUser(String name, String email, String password) {
        User existing = userRepository.findByEmail(email);
        if (existing != null) return null;
        String hashed = Hasher.hashPassword(password);
        User newUser = new User(0, name, email, hashed);
        return userRepository.save(newUser);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;
        return Hasher.checkPassword(password, user.getPassword()) ? user : null;
    }

    public List<Booking> getUserBookings(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Room createRoom(Room room) {
        return roomRepository.create(room);
    }

    public boolean updateRoom(Room room) {
        return roomRepository.update(room);
    }

    public boolean deleteRoom(int roomId, int hotelId) {
        return roomRepository.delete(roomId, hotelId);
    }

    public List<Booking> getBookingsByHotel(int hotelId) {
        return bookingRepository.findByHotelId(hotelId);
    }

    public boolean cancelBooking(int bookingId) {
        return bookingRepository.cancel(bookingId);
    }

    public List<Hotel> searchHotels(String query) {
        if (query == null || query.trim().isEmpty()) return hotelRepository.findAll();
        return hotelRepository.search(query.trim());
    }
}
