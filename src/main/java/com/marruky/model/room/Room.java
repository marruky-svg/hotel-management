package com.marruky.model.room;

import com.marruky.interfaces.ICalculable;

public abstract class Room implements ICalculable {

    public enum Type {
        SINGLE,
        DOUBLE,
        SUITE,
        PRESIDENTIAL
    }

    protected int id;
    protected String number;
    protected Type type;
    protected double nightPrice;
    protected String description;
    protected int floor;
    protected int capacity;
    protected boolean available;


    public Room(int id, String number, Type type, double nightPrice,
                String description, int floor, int capacity, boolean available) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.nightPrice = nightPrice;
        this.description = description;
        this.floor = floor;
        this.capacity = capacity;
        this.available = available;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getNightPrice() {
        return nightPrice;
    }

    public void setNightPrice(double nightPrice) {
        this.nightPrice = nightPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public abstract double calculatePrice(int numNights);

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", type=" + type +
                ", nightPrice=" + nightPrice +
                ", description='" + description + '\'' +
                ", floor=" + floor +
                ", capacity=" + capacity +
                ", available=" + available +
                '}';
    }
}
