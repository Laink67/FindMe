package com.example.potap.findme.model;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public class Event {

    private String description;

    private LatLng position;

    private Address address;


    private int usersCount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public Event(String description, LatLng position, Address address, int usersCount) {
        this.description = description;
        this.position = position;
        this.address = address;
        this.usersCount = usersCount;
    }


}
