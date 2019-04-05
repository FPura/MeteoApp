package ch.supsi.dti.isin.meteoapp.model;

import android.support.annotation.NonNull;

import java.util.UUID;

public class Location {
    private UUID Id;
    private String name = null;

    private double longitude;
    private double latitude;
    private Weather weather = null;
    private boolean isCurrentLocation = false;


    private boolean nameChanged = false;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Location() {
        Id = UUID.randomUUID();
    }

    public Location(final UUID Id, final String mName) {
        this.Id = Id;
        this.name = mName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(final Weather weather) {
        this.weather = weather;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + "  Id: " + Id;
    }

    public boolean isNameChanged() {
        return nameChanged;
    }

    public void setNameChanged(boolean nameChanged) {
        this.nameChanged = nameChanged;
    }

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }

    public void setCurrentLocation(boolean currentLocation) {
        isCurrentLocation = currentLocation;
    }
}