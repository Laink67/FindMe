package com.example.potap.findme.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class Event {

    private int id;

    private String title;

    private String description;

    private LatLng position;

    private String address;

    private String userId;

    private int usersCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public Event(int id, String title, String description, Map position, String address, int usersCount,String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.position = new LatLng((double) position.get("latitude"),(double) position.get("longitude"));
        this.address = address;
        this.usersCount = usersCount;
        this.userId = userId;
    }

    public Event(int id, String title, String description, LatLng position, String address, int usersCount,String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.position = position;
        this.address = address;
        this.usersCount = usersCount;
        this.userId = userId;
    }
}
