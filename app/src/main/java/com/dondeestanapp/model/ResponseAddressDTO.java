package com.dondeestanapp.model;

import com.dondeestanapp.ui.api.AddressService;

import java.io.Serializable;

import retrofit2.Call;

public class ResponseAddressDTO implements AddressService, Serializable {

    private Integer id;
    private String street;
    private String number;
    private String floor;
    private String apartament;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private String latitude;
    private String longitude;
    private Integer observerUserId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getApartament() {
        return apartament;
    }

    public void setApartament(String apartament) {
        this.apartament = apartament;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getObserverUserId() {
        return observerUserId;
    }

    public void setObserverUserId(Integer observerUserId) {
        this.observerUserId = observerUserId;
    }

    @Override
    public Call<ServerResponse> getAddressesById(Integer userId) {
        return null;
    }

    @Override
    public Call<ServerResponse> setAddressInObserverUser(String street, String number, String floor,
                                                         String apartament, String zipCode,
                                                         String city, String state, String country,
                                                         String latitude, String longitude,
                                                         Integer observerUserId) {
        return null;
    }

    @Override
    public Call<ServerResponse> deleteAddressById(Integer id) {
        return null;
    }
}
