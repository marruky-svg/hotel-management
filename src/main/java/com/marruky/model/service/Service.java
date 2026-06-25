package com.marruky.model.service;

import com.marruky.interfaces.ICalculable;

public abstract class Service implements ICalculable {

    public enum Type{
        ROOM_SERVICE,
        SPA,
        LAUNDRY,
        AIRPORT_TRANSFER,
        MINIBAR,
        BREAKFAST,
        DINNER,
        SNACKS
    }

    protected int id;
    protected String name;
    protected Type type;
    protected double price;
    protected String description;

    @Override
     public abstract double calculatePrice(int amount);

    public Service(int id, String name, Type type, double price, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }

    public static class AirportTransfer extends Service {

        public AirportTransfer(int id, String name, Type type, double price, String description) {
            super(id, name, type, price, description);
        }

        @Override
        public double calculatePrice(int amount) {
            return price * amount;
        }
    }
}
