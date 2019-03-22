package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.activities.MainActivity;

public class LocationsHolder {

    private static LocationsHolder sLocationsHolder;

    private List<Location> mLocations;

    public static LocationsHolder get() {
        if (sLocationsHolder == null)
            sLocationsHolder = new LocationsHolder();

        return sLocationsHolder;
    }

    public static void addLocations(List<Location> mLocations) {
        if (sLocationsHolder == null) {
            sLocationsHolder = new LocationsHolder(mLocations);
            return;
        }

        sLocationsHolder.addAll(mLocations);
    }

    private LocationsHolder() {
        mLocations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Location location = new Location();
            location.setName("Location # " + i);
            mLocations.add(location);
        }
    }

    private LocationsHolder(List<Location> mLocations) {
        this.mLocations = new ArrayList<>(mLocations);
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    private void addAll(List<Location> mLocations) {
        this.mLocations.addAll(mLocations);
    }
    public Location getLocation(UUID id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }
        return null;
    }
}
