package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.Weather;

public class WeatherTask extends AsyncTask<Void, Void, Location> {

    private OnTaskCompletedLocations listener;
    private Location location;

    public WeatherTask(OnTaskCompletedLocations listener, Location location) {
        this.listener = listener;
        this.location = location;
    }

    // Create weather object, add it on the location and return it
    @Override
    protected Location doInBackground(Void... voids) {
        // Request the API location weather
        Weather weather = new WeatherFetch().fetchItems(location.getName());

        // Set it on Location
        location.setWeather(weather);
        return location;
    }

    // Call onTaskCompleted at the end
    @Override
    protected void onPostExecute(Location location) {
        if (listener != null)
            listener.onTaskCompleted(location);
    }
}