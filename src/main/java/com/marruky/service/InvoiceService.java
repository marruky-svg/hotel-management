package com.marruky.service;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.Invoice;
import com.marruky.repository.PersonRepository;
import com.marruky.repository.RoomRepository;
import com.marruky.repository.ServiceRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class InvoiceService {

    public Invoice calculateInvoice(int reservationId){

        double totalFinal;
        double totalRooms = 0;
        double totalServices = 0;
        double discount;

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservations WHERE id=?")){

            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                throw new NotFoundException("Reservation not found");
            }

            var roomOptional = new RoomRepository().findById(rs.getInt("room_id"));
            if (roomOptional.isPresent()) {
                totalRooms = roomOptional.get().calculatePrice(rs.getInt("num_nights"));
            }

            var reservationServices = new ServiceRepository().findByReservationId(reservationId);
            for (var reservationService : reservationServices) {
                totalServices += reservationService.getService().calculatePrice(reservationService.getAmount());
            }

            var client = new PersonRepository().findById(rs.getInt("client_id"));
            double subtotal = totalRooms + totalServices;
            discount = subtotal * client.calculateDiscount();
            totalFinal = subtotal - discount;

            return new Invoice(0, reservationId, totalRooms, totalServices, discount, totalFinal, LocalDateTime.now());
        }catch (SQLException e){
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}
