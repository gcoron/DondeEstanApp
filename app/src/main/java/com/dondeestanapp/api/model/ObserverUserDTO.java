package com.dondeestanapp.api.model;

import java.util.List;

public class ObserverUserDTO {

    private int userId;
    private String name;
    private String lastName;
    private String numberId;
    private String email;
    private String username;
    private String password;
    private String childsName;
    private Integer userObserveeId;
    private String userObserveePrivacyKey;
    private Location lastLocation;
    private List<Address> addresses;

    public Integer getUserObserveeId() {
        return userObserveeId;
    }

    public void setUserObserveeId(Integer userObserveeId) {
        this.userObserveeId = userObserveeId;
    }

    public String getUserObserveePrivacyKey() {
        return userObserveePrivacyKey;
    }

    public void setUserObserveePrivacyKey(String userObserveeId) {
        this.userObserveePrivacyKey = userObserveeId;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getNumberId() {
        return numberId;
    }

    public void setNumberId(String numberId) {
        this.numberId = numberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChildsName() {
        return childsName;
    }

    public void setChildsName(String childsName) {
        this.childsName = childsName;
    }
}
