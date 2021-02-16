package com.dondeestanapp.api.model;

import com.dondeestanapp.api.LoginRegisterService;

import retrofit2.Call;

public class ResponseLoginRegisterDTO implements LoginRegisterService {

    private int userId;
    private int numberId;
    private String name;
    private String lastName;
    private String username;
    private String user;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNumberId() {
        return numberId;
    }

    public void setNumberId(int numberId) {
        this.numberId = numberId;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return user;
    }

    public void setUserType(String user) {
        this.user = user;
    }

    @Override
    public Call<ServerResponse> userLogin(String username, String password) {
        return null;
    }

    @Override
    public Call<ServerResponse> registerUserObservee(String name, String lastName, String numberId,
                                                     String email, String username,
                                                     String password, String company,
                                                     String licensePlate, String carRegistration) {
        return null;
    }

    @Override
    public Call<ServerResponse> registerObserverUser(String name, String lastName, String numberId,
                                                     String email, String username, String password,
                                                     String childName,
                                                     String userObserveePrivacyKey) {
        return null;
    }
}
