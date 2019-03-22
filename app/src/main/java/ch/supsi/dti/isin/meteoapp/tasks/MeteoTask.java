package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;

import java.util.List;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.Weather;

public class MeteoTask extends AsyncTask<Void, Void, Weather> {

    private String apiKey = "815504bb440299e3ebbb76868cbc7c47";
    private OnTaskCompleted listener;
    private Location location;

    public MeteoTask(OnTaskCompleted listener, Location location) {
        this.listener = listener;
        this.location = location;
    }

    @Override
    protected Weather doInBackground(Void... voids) { // chiamo doInBackground() senza parametri
        return new MeteoFetcher().fetchItems(apiKey, location.getName());
    }
    @Override
    protected void onPostExecute(Weather weather) {
        listener.onTaskCompleted(weather); // alla fine del Task richiamo il metodo onTaskCompleted() del listener
    }
}