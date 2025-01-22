package com.singlesoft.repaircon.models;

import java.io.Serializable;

public class Customer implements Serializable {
    private Long id;
    private String name;

    private String contact;
    private String address;

    private int numServices;

    // Getters and setters

    public int getNumServices() {
        return numServices;
    }

    public void setNumServices(int numServices) {
        this.numServices = numServices;
    }

    public Long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = (long) id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}