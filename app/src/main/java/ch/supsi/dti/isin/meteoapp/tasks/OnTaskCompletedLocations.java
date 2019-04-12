package ch.supsi.dti.isin.meteoapp.tasks;

import ch.supsi.dti.isin.meteoapp.model.Location;

public interface OnTaskCompletedLocations {
    void onTaskCompleted(Location location);

    void onTaskCompletedCoordinate(Location location);
}