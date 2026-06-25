package com.marruky.model.service;

public class Laundry extends Service {

    public Laundry(int id, String name, Type type, double price, String description) {
        super(id, name, type, price, description);
    }

    @Override
    public double calculatePrice(int amount){
        return price * amount;
    }
}
