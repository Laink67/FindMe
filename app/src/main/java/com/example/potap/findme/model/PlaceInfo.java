package com.example.potap.findme.model;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String id;
    private String name;
    private String address;
    private LatLng latLng;

    public PlaceInfo(String id, String name, String address, LatLng latLng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latLng = latLng;
    }

    public PlaceInfo(){}

    public PlaceInfo(Place place){
        this.id = place.getId();
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.latLng = place.getLatLng();
    }


    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
