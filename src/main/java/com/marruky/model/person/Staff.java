package com.marruky.model.person;

public class Staff extends Person {

    protected String position;
    protected double salary;
    protected String shift;

    public Staff(int id, String name, String email, String contact, String nif, Person.Type type,
                 String position, double salary, String shift){
        super(id, name, email, contact, nif, type);
        this.position = position;
        this.salary = salary;
        this.shift = shift;
    }
    @Override
    public double calculateDiscount(){
        return 0.0;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
