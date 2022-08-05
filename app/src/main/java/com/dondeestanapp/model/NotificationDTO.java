package com.dondeestanapp.model;


public class NotificationDTO {

    private String notificationId;
    private String title;
    private String description;
    private UserObserveeDTO userObserveeDTO;

    public NotificationDTO(String notificationId, String title, String description, UserObserveeDTO userObserveeDTO) {
        this.notificationId = notificationId;
        this.title = title;
        this.description = description;
        this.userObserveeDTO = userObserveeDTO;
    }

    public NotificationDTO() {
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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

    public UserObserveeDTO getUserObserveeDTO() {
        return userObserveeDTO;
    }

    public void setUserObserveeDTO(UserObserveeDTO userObserveeDTO) {
        this.userObserveeDTO = userObserveeDTO;
    }
}
