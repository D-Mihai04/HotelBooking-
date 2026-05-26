package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseFactory {

    private static final String URL  = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "Zlatanibra9*";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS hotels (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                address VARCHAR(200),
                phone VARCHAR(30),
                email VARCHAR(100)
            );

            CREATE TABLE IF NOT EXISTS rooms (
                id SERIAL PRIMARY KEY,
                hotel_id INTEGER NOT NULL REFERENCES hotels(id),
                room_number VARCHAR(10) NOT NULL,
                type VARCHAR(20) NOT NULL DEFAULT 'SINGLE',
                capacity INTEGER NOT NULL DEFAULT 1,
                price_per_night NUMERIC(10,2) NOT NULL,
                available BOOLEAN NOT NULL DEFAULT TRUE
            );

            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                password VARCHAR(100) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS bookings (
                booking_id SERIAL PRIMARY KEY,
                customer_id INTEGER NOT NULL REFERENCES users(id),
                room_id INTEGER NOT NULL REFERENCES rooms(id),
                check_in_date DATE NOT NULL,
                check_out_date DATE NOT NULL,
                CONSTRAINT check_dates CHECK (check_out_date > check_in_date)
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    public static void seedSampleData() {
        String checkSql = "SELECT COUNT(*) FROM hotels";
        String seedSql = """
            INSERT INTO hotels (name, address, phone, email) VALUES
            ('Radisson Blu Hotel Cluj', 'Aleea Stadionului 1, Cluj-Napoca, Romania', '+40-364-431800','info.cluj@radissonblu.com'),
            ('Grand Hotel Italia', 'Strada Vasile Conta 2, Cluj-Napoca, Romania', '+40-364-111333','reservations@grandhotelitalia.ro'),
            ('Hotel Beyfin', 'Piața Avram Iancu 3, Cluj-Napoca, Romania','+40-264-403804', 'office@hotelbeyfin.ro'),
            ('Hampton by Hilton Cluj-Napoca', 'Bulevardul 21 Decembrie 1989 67, Cluj-Napoca, Romania','+40-372-778800', 'clujnapoca.hampton@hilton.com'),
            ('Hotel Platinia', 'Calea Mănăștur 2-6, Cluj-Napoca, Romania', '+40-364-730000', 'office@hotelplatinia.ro');

            INSERT INTO rooms (hotel_id, room_number, type, capacity, price_per_night) VALUES
            (1, '101', 'SINGLE', 1, 89.99),
            (1, '102', 'SINGLE', 1, 89.99),
            (1, '201', 'DOUBLE', 2, 149.99),
            (1, '202', 'DOUBLE', 2, 149.99),
            (1, '301', 'SUITE', 4, 299.99);

            INSERT INTO rooms (hotel_id, room_number, type, capacity, price_per_night) VALUES
            (2, 'A1', 'SINGLE', 1, 109.99),
            (2, 'A2', 'DOUBLE', 2, 179.99),
            (2, 'B1', 'DOUBLE', 2, 179.99),
            (2, 'B2', 'SUITE', 4, 349.99),
            (2, 'P1', 'SUITE', 6, 499.99);

            INSERT INTO rooms (hotel_id, room_number, type, capacity, price_per_night) VALUES
            (3, 'C1', 'SINGLE', 1, 69.99),
            (3, 'C2', 'SINGLE', 1, 69.99),
            (3, 'C3', 'DOUBLE', 2, 119.99),
            (3, 'C4', 'SUITE', 3, 199.99);
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery(checkSql);
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Sample data already exists, skipping seed.");
                return;
            }
            stmt.execute(seedSql);
            System.out.println("Sample data seeded.");
        } catch (SQLException e) {
            System.err.println("Failed to seed sample data: " + e.getMessage());
        }
    }
}
