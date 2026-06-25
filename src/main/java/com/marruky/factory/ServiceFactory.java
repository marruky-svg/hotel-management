package com.marruky.factory;

import com.marruky.exception.HotelException;
import com.marruky.model.service.*;
import com.marruky.model.service.roomservice.Breakfast;
import com.marruky.model.service.roomservice.Dinner;
import com.marruky.model.service.roomservice.RoomService;
import com.marruky.model.service.roomservice.Snacks;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ServiceFactory {

    public static Service createService(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String name = rs.getString("name");
        Service.Type type = Service.Type.valueOf(rs.getString("type"));
        double price = rs.getDouble("price");
        String description = rs.getString("description");

        return switch (type){
            case Service.Type.AIRPORT_TRANSFER -> new AirportTransfer(id, name, type, price, description);
            case Service.Type.SPA -> new Spa(id, name, type, price, description);
            case Service.Type.LAUNDRY -> new Laundry(id, name, type, price, description);
            case Service.Type.MINIBAR -> new Minibar(id, name, type, price, description);
            default -> throw new HotelException("Service is not available");
        };
    }

    public static RoomService createRoomService(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String name = rs.getString("name");
        Service.Type type = Service.Type.valueOf(rs.getString("type"));
        double price = rs.getDouble("price");
        String description = rs.getString("description");
        LocalDateTime deliveryTime = rs.getTimestamp("delivery_time").toLocalDateTime();
        String roomNumber = rs.getString("room_number");

        return switch (type){
            case BREAKFAST -> new Breakfast(id, name, type, price, description, deliveryTime, roomNumber);
            case DINNER ->  new Dinner(id, name, type, price, description, deliveryTime, roomNumber);
            case SNACKS -> new Snacks(id, name, type, price, description, deliveryTime, roomNumber);
            default -> throw new HotelException("Room service is not available");
        };
    }
}
