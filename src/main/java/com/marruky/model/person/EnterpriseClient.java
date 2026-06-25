package com.marruky.model.person;

public class EnterpriseClient extends Client{

    public EnterpriseClient(int id, String name, String email, String contact, String nif, Person.Type type, int loyaltyPoints, String enterprise){
        super(id, name, email, contact, nif, type, loyaltyPoints, enterprise);
        pointsMultiplier = 2;
    }
}
