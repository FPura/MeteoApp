package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.Weather;

public class MeteoTaskCoord extends AsyncTask<Void, Void, Location> {

    private String apiKey = "815504bb440299e3ebbb76868cbc7c47";
    private OnTaskCompleted listener;
    private Location location;

    public MeteoTaskCoord(OnTaskCompleted listener, Location location) {
        this.listener = listener;
        this.location = location;
    }

    @Override
    protected Location doInBackground(Void... voids) { // chiamo doInBackground() senza parametri
        Weather weatehr = new MeteoFetcher().fetchItemsCoord(apiKey, location.getLongitude(), location.getLatitude());
        location.setWeather(weatehr);
        return location;
    }
    @Override
    protected void onPostExecute(Location newLocation) {
        listener.onTaskCompleted(newLocation); // alla fine del Task richiamo il metodo onTaskCompleted() del listener
    }
}