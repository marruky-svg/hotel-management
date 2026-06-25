package com.marruky.repository;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.factory.ServiceFactory;
import com.marruky.model.ReservationService;
import com.marruky.model.service.Service;
import com.marruky.model.service.roomservice.RoomService;

import java.sql.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {

    public List<Service> findAll() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM services");
             ResultSet rs = stmt.executeQuery()) {

            List<Service> services = new ArrayList<>();
            while (rs.next()) {
                Service.Type type = Service.Type.valueOf(rs.getString("type"));
                if (type == Service.Type.BREAKFAST
                        || type == Service.Type.DINNER
                        || type == Service.Type.SNACKS) {
                    services.add(ServiceFactory.createRoomService(rs));
                } else {
                    services.add(ServiceFactory.createService(rs));
                }
            }
            return services;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Service findById(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM services WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Service.Type type = Service.Type.valueOf(rs.getString("type"));
                if (type == Service.Type.BREAKFAST
                        || type == Service.Type.DINNER
                        || type == Service.Type.SNACKS) {
                    return (ServiceFactory.createRoomService(rs));
                } else {
                    return (ServiceFactory.createService(rs));
                }
            }
            throw new NotFoundException("Service wasn't found");
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public List<ReservationService> findByReservationId(int reservationId) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT s.*, sr.amount, sr.order_date  FROM services s " +
                     "INNER JOIN services_reservations sr " +
                     "ON s.id = sr.service_id " +
                     "WHERE sr.reserve_id = ?")) {

            List<ReservationService> reservationServices = new ArrayList<>();
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Service.Type type = Service.Type.valueOf(rs.getString("type"));
                Service service;
                if (type == Service.Type.BREAKFAST
                        || type == Service.Type.DINNER
                        || type == Service.Type.SNACKS) {
                    service = ServiceFactory.createRoomService(rs);
                } else {
                    service = ServiceFactory.createService(rs);
                }
                int amount = rs.getInt("amount");
                LocalDateTime orderDate = rs.getTimestamp("order_date").toLocalDateTime();
                reservationServices.add(new ReservationService(service, amount, orderDate));
            }
            return reservationServices;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int saveService(Service service) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO services (name, type, price, description) VALUES(?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, service.getName());
            stmt.setString(2, service.getType().toString());
            stmt.setDouble(3, service.getPrice());
            stmt.setString(4, service.getDescription());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new DataBaseException("The service couldn't be created");

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int saveServiceRoom(RoomService roomService) {
        Connection conn = DataBaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO services (name, type, price, description) VALUES(?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                try {
                    stmt.setString(1, roomService.getName());
                    stmt.setString(2, roomService.getType().toString());
                    stmt.setDouble(3, roomService.getPrice());
                    stmt.setString(4, roomService.getDescription());
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    rs.next();
                    int serviceId = rs.getInt(1);

                    try (PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO room_services(service_id, delivery_time, room_number) VALUES (?,?,?) ",
                            PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmt1.setInt(1, serviceId);
                        stmt1.setTimestamp(2, Timestamp.valueOf(roomService.getDeliveryTime()));
                        stmt1.setString(3, roomService.getRoomNumber());
                        stmt1.executeUpdate();
                        conn.commit();
                        ResultSet rs1 = stmt1.getGeneratedKeys();
                        rs1.next();
                        return rs1.getInt(1);
                    }

                } catch (SQLException e) {
                    conn.rollback();
                    throw new DataBaseException(e.getMessage(), e);
                }
            } catch (SQLException e) {
                conn.close();
                throw new DataBaseException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void delete(int id) {
        Connection conn = DataBaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            Service service = findById(id);
            if (service instanceof RoomService) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM room_services WHERE service_id = ?")) {
                    stmt.setInt(1, service.getId());
                    stmt.executeUpdate();
                    try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM services WHERE id = ?")) {
                        stmt1.setInt(1, service.getId());
                        stmt1.executeUpdate();
                        conn.commit();
                    } catch (SQLException e) {
                        conn.rollback();
                        throw new DataBaseException(e.getMessage(), e);
                    }
                }
            }else{
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM services WHERE id = ?")) {
                    stmt.setInt(1, service.getId());
                    stmt.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new DataBaseException(e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void addServiceToReservation(int reservationId, int serviceId, int amount){
        try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO services_reservations (reserve_id, service_id, amount) VALUES(?,?,?)")){

            stmt.setInt(1, reservationId);
            stmt.setInt(2, serviceId);
            stmt.setInt(3, amount);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}
