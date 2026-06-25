package com.marruky.model.service;

public class Minibar extends Service {

    public Minibar(int id, String name, Service.Type type, double price, String description) {
        super(id, name, type, price, description);
    }

    @Override
    public double calculatePrice(int amount){
        return price * amount;
    }

}
