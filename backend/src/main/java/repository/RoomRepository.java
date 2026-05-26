package repository;

import model.Room;
import org.example.DatabaseFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomRepository {

    public List<Room> findByHotelId(int hotelId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, hotel_id, room_number, type, capacity, price_per_night, available " +
                     "FROM rooms WHERE hotel_id = ? ORDER BY room_number";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rooms.add(mapRoom(rs));

        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    public Room findById(int id) {
        String sql = "SELECT id, hotel_id, room_number, type, capacity, price_per_night, available " +
                     "FROM rooms WHERE id = ?";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRoom(rs);

        } catch (SQLException e) {
            System.err.println("Error fetching room: " + e.getMessage());
        }
        return null;
    }

    public List<Room> findAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.id, r.hotel_id, r.room_number, r.type, r.capacity, r.price_per_night, r.available " +
                     "FROM rooms r " +
                     "WHERE r.hotel_id = ? AND r.available = true " +
                     "AND r.id NOT IN (" +
                     "  SELECT b.room_id FROM bookings b " +
                     "  WHERE b.check_in_date < ? AND b.check_out_date > ?" +
                     ") ORDER BY r.room_number";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hotelId);
            stmt.setDate(2, Date.valueOf(checkOut));
            stmt.setDate(3, Date.valueOf(checkIn));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) rooms.add(mapRoom(rs));

        } catch (SQLException e) {
            System.err.println("Error fetching available rooms: " + e.getMessage());
        }
        return rooms;
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE room_id = ? AND check_in_date < ? AND check_out_date > ?";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, Date.valueOf(checkOut));
            stmt.setDate(3, Date.valueOf(checkIn));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;

        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
        }
        return false;
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("id"),
                rs.getInt("hotel_id"),
                rs.getString("room_number"),
                rs.getString("type"),
                rs.getInt("capacity"),
                rs.getDouble("price_per_night"),
                rs.getBoolean("available")
        );
    }

    public Room create(Room room) {
        String sql = "INSERT INTO rooms (hotel_id, room_number, type, capacity, price_per_night, available) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getHotelId());
            stmt.setString(2, room.getRoomNumber());
            stmt.setString(3, room.getType());
            stmt.setInt(4, room.getCapacity());
            stmt.setDouble(5, room.getPricePerNight());
            stmt.setBoolean(6, room.isAvailable());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) room.setId(rs.getInt("id"));
        } catch (SQLException e) {
            System.err.println("Error creating room: " + e.getMessage());
        }
        return room;
    }

    public boolean update(Room room) {
        String sql = "UPDATE rooms SET room_number=?, type=?, capacity=?, price_per_night=?, available=? WHERE id=? AND hotel_id=?";
        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType());
            stmt.setInt(3, room.getCapacity());
            stmt.setDouble(4, room.getPricePerNight());
            stmt.setBoolean(5, room.isAvailable());
            stmt.setInt(6, room.getId());
            stmt.setInt(7, room.getHotelId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int roomId, int hotelId) {
        String sql = "DELETE FROM rooms WHERE id=? AND hotel_id=?";
        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, hotelId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }
}
