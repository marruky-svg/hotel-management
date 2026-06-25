package com.marruky.model;

import com.marruky.model.service.Service;

import java.time.LocalDateTime;

public class ReservationService {

    private Service service;
    private int amount;
    private LocalDateTime orderDate;

    public ReservationService(Service service, int amount, LocalDateTime orderDate) {
        this.service = service;
        this.amount = amount;
        this.orderDate = orderDate;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "ReservationService{" +
                "service=" + service +
                ", amount=" + amount +
                ", orderDate=" + orderDate +
                '}';
    }
}
