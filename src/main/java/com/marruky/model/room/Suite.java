package com.marruky.model.room;

public class Suite extends Room{

    public Suite(int id, String number, Type type, double nightPrice,
                 String description, int floor, int capacity, boolean available) {
        super(id, number, type, nightPrice, description, floor, capacity, available);
    }

    @Override
    public double calculatePrice(int numNights){
        return numNights * nightPrice * 1.2;
    }

    @Override
    public String toString() {
        return "Suite{" +
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
