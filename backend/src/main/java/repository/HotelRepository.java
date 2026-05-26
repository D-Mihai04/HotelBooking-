package repository;

import model.Hotel;
import org.example.DatabaseFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HotelRepository {

    public List<Hotel> findAll() {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT id, name, address, phone, email FROM hotels ORDER BY id";

        try (Connection conn = DatabaseFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("id"),
                        new ArrayList<>(),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotels: " + e.getMessage());
        }
        return hotels;
    }

    public Hotel findById(int id) {
        String sql = "SELECT id, name, address, phone, email FROM hotels WHERE id = ?";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Hotel(
                        rs.getInt("id"),
                        new ArrayList<>(),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotel: " + e.getMessage());
        }
        return null;
    }

    public List<Hotel> search(String query) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT id, name, address, phone, email FROM hotels " +
                "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(address) LIKE LOWER(?) " +
                "ORDER BY id";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("id"),
                        new ArrayList<>(),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching hotels: " + e.getMessage());
        }
        return hotels;
    }
}
