package com.marruky.repository;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class InvoiceRepository {

    public Optional<Invoice> findByReservationId(int reservationId) {

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoices WHERE reserve_id = ?")) {

            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Invoice(rs.getInt("id"), rs.getInt("reserve_id"),
                        rs.getDouble("total_rooms"), rs.getDouble("total_services"),
                        rs.getDouble("discount"), rs.getDouble("total_final"),
                        rs.getTimestamp("issued_at").toLocalDateTime()));
            }else{
                return Optional.empty();
            }


        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int save(Invoice invoice){
        try(Connection conn = DataBaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO invoices (reserve_id, total_rooms, total_services " +
                ",discount, total_final) VALUES(?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1, invoice.getReservationId());
            stmt.setDouble(2, invoice.getTotalRooms());
            stmt.setDouble(3, invoice.getTotalServices());
            stmt.setDouble(4, invoice.getDiscount());
            stmt.setDouble(5,invoice.getTotalFinal());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }
            throw new DataBaseException("Invoice couldn't be saved");

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(),e);
        }
    }


}
