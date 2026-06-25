package com.marruky.model.service.roomservice;

import java.time.LocalDateTime;

public class Dinner extends RoomService {


    public Dinner(int id, String name, Type type,
                  double price, String description,
                  LocalDateTime deliveryTime, String roomNumber){

        super(id, name, type, price, description, deliveryTime, roomNumber);
    }

    @Override
    public double calculatePrice(int amount){
        return price * amount;
    }

}
