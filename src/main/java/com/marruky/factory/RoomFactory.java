package com.marruky.factory;

import com.marruky.model.room.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomFactory {

    public static Room create(ResultSet rs) throws SQLException{
        int id = rs.getInt("id");
        String number = rs.getString("number");
        Room.Type type = Room.Type.valueOf(rs.getString("type"));
        double nightPrice = rs.getDouble("night_price");
        String description = rs.getString("description");
        int floor = rs.getInt("floor");
        int capacity = rs.getInt("capacity");
        boolean available = rs.getBoolean("available");

        return getRoom(id, number, type, nightPrice, description, floor, capacity, available);
    }

    public static Room create(int id, String number, Room.Type type,
                              double nightPrice, String description,
                              int floor, int capacity, boolean available) {
        return getRoom(id, number, type, nightPrice, description, floor, capacity, available);
    }


    private static Room getRoom(int id, String number, Room.Type type, double nightPrice, String description, int floor, int capacity, boolean available) {
        return switch (type){
            case SINGLE -> new SingleRoom(id, number, type,nightPrice, description, floor, capacity, available);
            case DOUBLE ->  new DoubleRoom(id, number, type,nightPrice, description, floor, capacity, available);
            case SUITE -> new Suite(id, number, type,nightPrice, description, floor, capacity, available);
            case PRESIDENTIAL -> new PresidentialSuite(id, number, type,nightPrice, description, floor, capacity, available);
            default -> throw new IllegalArgumentException("Unknown room type: " + type);
        };
    }
}
