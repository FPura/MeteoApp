package ch.supsi.dti.isin.meteoapp.model;

import java.util.UUID;

public class Location {
    public UUID Id;
    public String mName;

    private double longitude;
    private double latitude;
    private Weather weather;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Location() {
        Id = UUID.randomUUID();
    }
    public Location(UUID Id, String mName) {
        this.Id = Id;
        this.mName = mName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "Name: " + mName + "  Id: " + Id;
    }
}