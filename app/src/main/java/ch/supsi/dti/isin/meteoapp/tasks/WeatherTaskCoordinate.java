package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;

import ch.supsi.dti.isin.meteoapp.model.Location;

public class WeatherTaskCoordinate extends AsyncTask<Void, Void, Location> {

    private OnTaskCompletedLocations listener;
    private Location location;

    public WeatherTaskCoordinate(OnTaskCompletedLocations listener, Location location) {
        this.listener = listener;
        this.location = location;
    }

    // Create weather object, add it on the location and return it
    @Override
    protected Location doInBackground(Void... voids) {
        new WeatherFetch().fetchItemsCoordinate(location.getLongitude(), location.getLatitude(), location);

        // Test location (Milan)
        // new WeatherFetch().fetchItemsCoordinate(45.465454, 9.186516, location);
        return location;
    }

    // Call onTaskCompleted at the end
    @Override
    protected void onPostExecute(Location newLocation) {
        if (listener != null)
            listener.onTaskCompletedCoordinate(newLocation);
    }
}