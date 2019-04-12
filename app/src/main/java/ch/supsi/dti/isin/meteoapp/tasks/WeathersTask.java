package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;

import java.util.List;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.Weather;

public class WeathersTask extends AsyncTask<Void, Void, Void> {

    private OnTaskCompletedLocations listener;
    private List<Location> locations;

    public WeathersTask(OnTaskCompletedLocations listener, List<Location> locations) {
        this.listener = listener;
        this.locations = locations;
    }

    // Create weather object, add it on the location and return it
    @Override
    protected Void doInBackground(Void... voids) {

        for (Location location : locations) {
            Weather weather = new WeatherFetch().fetchItems(location.getName());
            location.setWeather(weather);
        }
        return null;
    }

    // Call onTaskCompleted at the end
    @Override
    protected void onPostExecute(Void ignore) {
        if (listener != null)
            listener.onTaskCompleted(null);
    }
}