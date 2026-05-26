package repository;

import model.Booking;
import org.example.DatabaseFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookingRepository {

    public Booking save(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date) " +
                     "VALUES (?, ?, ?, ?) RETURNING booking_id";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getCustomerID());
            stmt.setInt(2, booking.getRoomID());
            stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                booking.setBookingID(rs.getInt("booking_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error saving booking: " + e.getMessage());
        }
        return booking;
    }

    public List<Booking> findByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT booking_id, customer_id, room_id, check_in_date, check_out_date " +
                     "FROM bookings WHERE customer_id = ? ORDER BY check_in_date";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
        }
        return bookings;
    }

    public List<Booking> findByHotelId(int hotelId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.customer_id, b.room_id, b.check_in_date, b.check_out_date " +
                "FROM bookings b JOIN rooms r ON b.room_id = r.id " +
                "WHERE r.hotel_id = ? ORDER BY b.check_in_date DESC";
        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("booking_id"), rs.getInt("customer_id"), rs.getInt("room_id"),
                        rs.getDate("check_in_date").toLocalDate(), rs.getDate("check_out_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotel bookings: " + e.getMessage());
        }
        return bookings;
    }

    public boolean cancel(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }
}
