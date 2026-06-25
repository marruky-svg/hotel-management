package com.marruky.model;

import java.time.LocalDateTime;

public class Invoice {

    private int id;
    private int reservationId;
    private double totalRooms;
    private double totalServices;
    private double discount;
    private double totalFinal;
    private LocalDateTime issuedAt;

    public Invoice(int id, int reservationId, double totalRooms, double totalServices,
                   double discount, double totalFinal,
                   LocalDateTime issuedAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.totalRooms = totalRooms;
        this.totalServices = totalServices;
        this.discount = discount;
        this.totalFinal = totalFinal;
        this.issuedAt = issuedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public double getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public double getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(int totalServices) {
        this.totalServices = totalServices;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(double totalFinal) {
        this.totalFinal = totalFinal;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", reservationId=" + reservationId +
                ", totalRooms=" + totalRooms +
                ", totalServices=" + totalServices +
                ", discount=" + discount +
                ", totalFinal=" + totalFinal +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
