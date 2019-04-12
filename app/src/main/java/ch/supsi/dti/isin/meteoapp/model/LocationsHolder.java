package ch.supsi.dti.isin.meteoapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationsHolder {

    private static LocationsHolder locationsHolder;

    private List<Location> locations;

    private LocationsHolder() {
        locations = new ArrayList<>();
    }

    private LocationsHolder(final List<Location> locations) {
        this.locations = new ArrayList<>(locations);
    }

    /*private void addAll(final List<Location> locations) {
        this.locations.addAll(locations);
    }*/

    public static LocationsHolder get() {
        if (locationsHolder == null)
            locationsHolder = new LocationsHolder();
        return locationsHolder;
    }

    public static void addLocation(final Location mLocation) {

        if (locationsHolder == null)
            locationsHolder = new LocationsHolder();

        locationsHolder.getLocations().add(mLocation);
    }

    /*
    public static void addLocations(final List<Location> locations) {

        if (locationsHolder == null)
            locationsHolder = new LocationsHolder();

        locationsHolder.getLocations().addAll(locations);
    }
    */

    public static void setLocations(final List<Location> locations) {
        locationsHolder = new LocationsHolder(locations);
    }

    public List<Location> getLocations() {
        return locations;
    }

    // Return UUID, we use it for DB search
    public Location getLocation(final UUID id) {

        for (Location location : locations) {
            if (location.getId().equals(id))
                return location;
        }
        return null;
    }

    public static boolean delete(String name) {
        if (locationsHolder == null)
            return false;

        for (Location loc : locationsHolder.getLocations()) {
            if (loc.getName().equals(name)) {
                locationsHolder.getLocations().remove(loc);
                return true;
            }
        }
        return false;
    }
}