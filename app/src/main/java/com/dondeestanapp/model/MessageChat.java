package com.dondeestanapp.model;

public class MessageChat {

    private String message;
    private String name;
    private String profile_picture;
    private String hour;

    public MessageChat() {
    }

    public MessageChat(String message, String name, String profile_picture, String hour) {
        this.message = message;
        this.name = name;
        this.profile_picture = profile_picture;
        this.hour = hour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
