package com.marruky.util;

import com.marruky.db.DataBaseConnection;
import com.marruky.model.User;
import com.marruky.model.person.Person;
import com.marruky.model.room.Room;
import com.marruky.model.service.Service;
import com.marruky.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Seeder {

    private final UserRepository userRepository = new UserRepository();

    public void seed() {
        seedUsers();
        seedRooms();
        seedServices();
        System.out.println("Seed data inserted successfully.");
    }

    private void seedUsers() {

        // ── Cliente normal ─────────────────────────────────────────────────
        int userId1 = userRepository.save(new User(0, "john_client", "client123", true, null));
        insertPerson(userId1, "John Smith", "john@hotel.com", "910000001", "123456789", Person.Type.CLIENT);
        int personId1 = getLastPersonId();
        insertClient(personId1, 0, null);

        // ── VIP Client ─────────────────────────────────────────────────────
        int userId2 = userRepository.save(new User(0, "vip_client", "vip123", true, null));
        insertPerson(userId2, "Alice VIP", "alice@hotel.com", "910000002", "234567891", Person.Type.VIP_CLIENT);
        int personId2 = getLastPersonId();
        insertClient(personId2, 650, null);

        // ── Enterprise Client ──────────────────────────────────────────────
        int userId3 = userRepository.save(new User(0, "enterprise_client", "enterprise123", true, null));
        insertPerson(userId3, "Bob Enterprise", "bob@hotel.com", "910000003", "345678912", Person.Type.ENTERPRISE_CLIENT);
        int personId3 = getLastPersonId();
        insertClient(personId3, 200, "TechCorp Ltd");

        // ── Receptionist ───────────────────────────────────────────────────
        int userId4 = userRepository.save(new User(0, "mary_staff", "staff123", true, null));
        insertPerson(userId4, "Mary Johnson", "mary@hotel.com", "910000004", "456789123", Person.Type.RECEPTIONIST);
        int personId4 = getLastPersonId();
        insertStaff(personId4, "Front Desk Receptionist", 1200.00, "MORNING");

        // ── Manager ────────────────────────────────────────────────────────
        int userId5 = userRepository.save(new User(0, "admin", "admin123", true, null));
        insertPerson(userId5, "Robert Admin", "admin@hotel.com", "910000005", "567891234", Person.Type.MANAGER);
        int personId5 = getLastPersonId();
        insertStaff(personId5, "General Manager", 2500.00, "MORNING");
    }

    private void seedRooms() {
        insertRoom("101", Room.Type.SINGLE,       50.00,  1, 1, "Cosy single room");
        insertRoom("102", Room.Type.DOUBLE,        80.00,  2, 1, "Comfortable double room");
        insertRoom("201", Room.Type.SUITE,        150.00,  3, 2, "Spacious suite with sea view");
        insertRoom("301", Room.Type.PRESIDENTIAL, 300.00,  4, 3, "Luxury presidential suite");
    }

    private void seedServices() {
        insertService("Spa Session",       Service.Type.SPA,              50.00, "Relaxing spa session");
        insertService("Laundry",           Service.Type.LAUNDRY,          10.00, "Laundry service per delivery");
        insertService("Airport Transfer",  Service.Type.AIRPORT_TRANSFER, 30.00, "Transfer to/from airport");
        insertService("Minibar",           Service.Type.MINIBAR,          20.00, "Minibar consumption");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void insertPerson(int userId, String name, String email,
                              String contact, String nif, Person.Type type) {
        String sql = "INSERT INTO people (user_id, name, email, contact, nif, type) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, contact);
            stmt.setString(5, nif);
            stmt.setString(6, type.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void insertClient(int personId, int loyaltyPoints, String enterprise) {
        String sql = "INSERT INTO clients (person_id, loyalty_points, enterprise) VALUES (?,?,?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personId);
            stmt.setInt(2, loyaltyPoints);
            stmt.setString(3, enterprise);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void insertStaff(int personId, String position, double salary, String shift) {
        String sql = "INSERT INTO staff (person_id, position, salary, shift) VALUES (?,?,?,?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personId);
            stmt.setString(2, position);
            stmt.setDouble(3, salary);
            stmt.setString(4, shift);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void insertRoom(String number, Room.Type type, double nightPrice,
                            int capacity, int floor, String description) {
        String sql = "INSERT INTO rooms (number, type, night_price, capacity, available, description, floor) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);
            stmt.setString(2, type.toString());
            stmt.setDouble(3, nightPrice);
            stmt.setInt(4, capacity);
            stmt.setBoolean(5, true);
            stmt.setString(6, description);
            stmt.setInt(7, floor);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void insertService(String name, Service.Type type, double price, String description) {
        String sql = "INSERT INTO services (name, type, price, description) VALUES (?,?,?,?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, type.toString());
            stmt.setDouble(3, price);
            stmt.setString(4, description);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getLastPersonId() {
        String sql = "SELECT id FROM people ORDER BY id DESC LIMIT 1";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("id");
            throw new RuntimeException("No person found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}