package com.marruky.model.person;

public class Receptionist extends Staff {

    public Receptionist(int id, String name, String email, String contact, String nif, Person.Type type,
                        String position, double salary, String shift){
        super(id, name, email, contact, nif, type, position, salary, shift);
    }

    @Override
    public double calculateDiscount(){return 0.10;}

}
