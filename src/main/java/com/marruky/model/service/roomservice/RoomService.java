package com.marruky.model.service.roomservice;

import com.marruky.model.service.Service;
import java.time.LocalDateTime;

public abstract class RoomService extends Service {

    protected LocalDateTime deliveryTime;
    protected String roomNumber;

    public RoomService(int id, String name, Type type,
                       double price, String description,
                       LocalDateTime deliveryTime, String roomNumber) {

        super(id, name, type, price, description);
        this.deliveryTime = deliveryTime;
        this.roomNumber = roomNumber;
    }

    public abstract double calculatePrice(int amount);


    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

}
