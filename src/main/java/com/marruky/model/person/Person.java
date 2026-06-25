package com.marruky.model.person;

public abstract class Person {

    public enum Type{
        CLIENT,
        VIP_CLIENT,
        ENTERPRISE_CLIENT,
        RECEPTIONIST,
        MANAGER
    }

    protected int id;
    protected String name;
    protected String email;
    protected String contact;
    protected String nif;
    protected Type type;

    public Person(int id, String name, String email, String contact, String nif, Type type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.nif = nif;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract double calculateDiscount();

    @Override
    public String toString(){
        return String.format("[%s] %s | %s | NIF: %s", type, name, email,nif);
    }
}
