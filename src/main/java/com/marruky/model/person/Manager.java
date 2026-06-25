package com.marruky.model.person;

public class Manager extends Staff{

    public Manager(int id, String name, String email, String contact, String nif, Person.Type type,
                   String position, double salary, String shift){
        super(id, name, email, contact, nif, type, position, salary, shift);
    }

    @Override
    public double calculateDiscount() {
        return 0.20;
    }
}
