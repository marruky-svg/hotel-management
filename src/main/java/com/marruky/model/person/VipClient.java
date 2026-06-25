package com.marruky.model.person;

public class VipClient extends Client{

    public VipClient(int id, String name, String email, String contact, String nif, Person.Type type, int loyaltyPoints, String enterprise){
        super(id, name, email, contact, nif, type, loyaltyPoints, enterprise);
        pointsMultiplier = 3;
    }

    @Override
    public String toString(){
        return super.toString() + " | VIP";
    }
}
