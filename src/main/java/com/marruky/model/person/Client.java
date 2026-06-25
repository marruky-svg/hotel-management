package com.marruky.model.person;

public class Client extends Person {

    protected int loyaltyPoints;
    protected String enterprise;
    protected int pointsMultiplier = 1;

    public Client(int id, String name, String email, String contact, String nif, Person.Type type, int loyaltyPoints, String enterprise) {
        super(id, name, email, contact, nif, type);
        this.loyaltyPoints = loyaltyPoints;
        this.enterprise = enterprise;
    }

    @Override
    public double calculateDiscount() {
        if (loyaltyPoints > 600) return 0.15;
        if (loyaltyPoints > 300) return 0.10;
        if (loyaltyPoints > 100) return 0.05;
        return 0.0;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("| Loyalty Points: %s | Enterprise: %s", loyaltyPoints, enterprise);
    }

    public void addPoints(int basePoints){
        this.loyaltyPoints += basePoints * pointsMultiplier;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public int getPointsMultiplier() {
        return pointsMultiplier;
    }

    public void setPointsMultiplier(int pointsMultiplier) {
        this.pointsMultiplier = pointsMultiplier;
    }
}
