package com.marruky.model.room;

public class SingleRoom extends Room {

    public SingleRoom(int id, String number, Type type, double nightPrice,
                      String description, int floor, int capacity, boolean available) {
        super(id, number, type, nightPrice, description, floor, capacity, available);
    }

    @Override
    public double calculatePrice(int numNights){
        return numNights * nightPrice;
    }

    @Override
    public String toString() {
        return "SingleRoom{" +
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
