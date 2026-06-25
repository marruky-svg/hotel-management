package com.marruky.model.room;

public class PresidentialSuite extends Room{

    public PresidentialSuite(int id, String number, Type type, double nightPrice,
                      String description, int floor, int capacity, boolean available) {
        super(id, number, type, nightPrice, description, floor, capacity, available);
    }

    @Override
    public double calculatePrice(int numNights){
        return numNights * nightPrice * 1.5;

    }

    @Override
    public String toString() {
        return "PresidentialSuite{" +
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
