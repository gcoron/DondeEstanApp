package com.dondeestanapp.api.model;

import com.dondeestanapp.api.DriverService;

import retrofit2.Call;

public class ResponseDriverDTO implements DriverService {

    private String name;
    private String lastName;
    private String privacyKey;
    private String companyName;
    private String licensePlate;
    private String carRegistration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrivacyKey() {
        return privacyKey;
    }

    public void setPrivacyKey(String privacyKey) {
        this.privacyKey = privacyKey;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCarRegistration() {
        return carRegistration;
    }

    public void setCarRegistration(String carRegistration) {
        this.carRegistration = carRegistration;
    }

    @Override
    public Call<ServerResponse> getDriverById(Integer userId) {
        return null;
    }

    @Override
    public Call<ServerResponse> setDriverInObserverUser(int userIdObserverUser, String userObserveePrivacyKey) {
        return null;
    }
}
