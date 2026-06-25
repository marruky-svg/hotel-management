package com.marruky.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    public enum State{
        PENDING,
        CONFIRMED,
        CHECKED_IN,
        CHECKED_OUT,
        CANCELLED
    }

    private int id;
    private int clientId;
    private int roomId;
    private int staffId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numNights;
    private State state;
    private String observations;
    private LocalDateTime createdAt;


    public Reservation(int id, int clientId, int roomId, int staffId,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       int numNights, State state, String observations,
                       LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.roomId = roomId;
        this.staffId = staffId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numNights = numNights;
        this.state = state;
        this.observations = observations;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumNights() {
        return numNights;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", roomId=" + roomId +
                ", staffId=" + staffId +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numNights=" + numNights +
                ", state=" + state +
                ", observations='" + observations + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
