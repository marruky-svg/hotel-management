package com.marruky.repository;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ReservationRepository {


    public List<Reservation> findAll() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservations");
             ResultSet rs = stmt.executeQuery()) {

            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                reservations.add(createReservation(rs));
            }
            return reservations;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Reservation findById(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservations WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createReservation(rs);
            }
            throw new NotFoundException("This ID doesn't exist");

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public List<Reservation> findByClientId(int clientId) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservations WHERE client_id = ?")) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                reservations.add(createReservation(rs));
            }
            return reservations;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int save(Reservation reservation) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO reservations (client_id, room_id, staff_id, check_in_date, check_out_date, " +
                     "state, observations) VALUES(?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reservation.getClientId());
            stmt.setInt(2, reservation.getRoomId());
            if(reservation.getStaffId() == 0) {
                stmt.setNull(3, Types.INTEGER);
            }else{
                stmt.setInt(3, reservation.getStaffId());
            }
            stmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setString(6, reservation.getState().toString());
            stmt.setString(7, reservation.getObservations());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void update(Reservation reservation) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE reservations SET check_in_date = ?, check_out_date = ?, " +
                     "observations = ?, room_id = ? WHERE id=?")) {

            stmt.setDate(1, Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(2, Date.valueOf(reservation.getCheckOutDate()));
            stmt.setString(3, reservation.getObservations());
            stmt.setInt(4, reservation.getRoomId());
            stmt.setInt(5, reservation.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void delete(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservations WHERE id=?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void updateState(int id, Reservation.State state) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE reservations SET state = ? WHERE id = ?")) {

            stmt.setString(1, state.toString());
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    private Reservation createReservation(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int clientId = rs.getInt("client_id");
        int roomId = rs.getInt("room_id");
        int staffId = rs.getInt("staff_id");
        LocalDate checkInDate = rs.getDate("check_in_date").toLocalDate();
        LocalDate checkOutDate = rs.getDate("check_out_date").toLocalDate();
        int numNights = rs.getInt("num_nights");
        Reservation.State state = Reservation.State.valueOf(rs.getString("state"));
        String observations = rs.getString("observations");
        LocalDateTime created_at = rs.getTimestamp("created_at").toLocalDateTime();

        return new Reservation(id, clientId, roomId, staffId,
                checkInDate, checkOutDate, numNights,
                state, observations, created_at);
    }
}
