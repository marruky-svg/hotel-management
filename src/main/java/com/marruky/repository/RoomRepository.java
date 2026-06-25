package com.marruky.repository;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.factory.RoomFactory;
import com.marruky.model.room.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepository {

    public List<Room> findAll() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rooms");
             ResultSet rs = stmt.executeQuery()) {

            List<Room> rooms = new ArrayList<>();
            while (rs.next()) {
                rooms.add(RoomFactory.create(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Optional<Room> findById(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rooms WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(RoomFactory.create(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public List<Room> findAvailable() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rooms WHERE available = true");
             ResultSet rs = stmt.executeQuery()) {

            List<Room> rooms = new ArrayList<>();
            while (rs.next()) {
                rooms.add(RoomFactory.create(rs));
            }
            return rooms;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int save(Room room) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO rooms (number, type, night_price, " +
                             "capacity, available, description, floor) VALUES(?,?,?,?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType().toString());
            stmt.setDouble(3, room.getNightPrice());
            stmt.setInt(4, room.getCapacity());
            stmt.setBoolean(5, room.isAvailable());
            stmt.setString(6, room.getDescription());
            stmt.setInt(7, room.getFloor());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

            throw new DataBaseException("Failed to retrieve generated key", null);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void update (Room room){
        try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE rooms SET number = ?, type = ?, night_price = ?, " +
                "capacity = ?, available = ?, description = ?, floor = ? WHERE id=?")){


            stmt.setString(1, room.getNumber());
            stmt.setString(2, room.getType().toString());
            stmt.setDouble(3, room.getNightPrice());
            stmt.setInt(4, room.getCapacity());
            stmt.setBoolean(5, room.isAvailable());
            stmt.setString(6, room.getDescription());
            stmt.setInt(7, room.getFloor());
            stmt.setInt(8, room.getId());

            stmt.executeUpdate();

        }catch (SQLException e){
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void delete (int id){
        try(Connection conn = DataBaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE id=?")){

            stmt.setInt(1,id);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}
