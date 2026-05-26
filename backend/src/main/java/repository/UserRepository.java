package repository;

import model.User;
import org.example.DatabaseFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserRepository {

    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role, hotel_id FROM users WHERE email = ?";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                u.setRole(rs.getString("role"));
                u.setHotelId(rs.getObject("hotel_id") != null ? rs.getInt("hotel_id") : 0);
                return u;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT id, name, email, password, role, hotel_id FROM users WHERE id = ?";
        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User(rs.getInt("id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("password"));
                u.setRole(rs.getString("role"));
                u.setHotelId(rs.getObject("hotel_id") != null ? rs.getInt("hotel_id") : 0);
                return u;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
        }
        return null;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
        return user;
    }
}
