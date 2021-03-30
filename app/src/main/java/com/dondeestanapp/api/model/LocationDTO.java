package com.dondeestanapp.api.model;


public class LocationDTO {

    private String latitude;
    private String longitude;
    private String dayHour;

    public LocationDTO(String latitude, String longitude, String dayHour) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dayHour = dayHour;
    }

    public LocationDTO() {
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

    public String getDayHour() {
        return dayHour;
    }

    public void setDayHour(String dayHour) {
        this.dayHour = dayHour;
    }
}
