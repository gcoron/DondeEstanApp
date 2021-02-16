package com.dondeestanapp.api.model;

public class ObserverUserDTO {

    private int userId;
    private String name;
    private String lastName;
    private String numberId;
    private String email;
    private String username;
    private String password;
    private String childsName;
    private UserObserveeDTO userObserveeDTO;

    public UserObserveeDTO getUserObserveeDTO() {
        return userObserveeDTO;
    }

    public void setUserObserveeDTO(UserObserveeDTO userObserveeDTO) {
        this.userObserveeDTO = userObserveeDTO;
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
