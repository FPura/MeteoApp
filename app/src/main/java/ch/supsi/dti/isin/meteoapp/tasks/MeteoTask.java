package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;

import java.util.List;

public class MeteoTask extends AsyncTask<Void, Void, List<String>> {

    private String apiKey = "815504bb440299e3ebbb76868cbc7c47";
    private OnTaskCompleted listener;
    public MeteoTask(OnTaskCompleted listener) {
        this.listener = listener;
    }
    @Override
    protected List<String> doInBackground(Void... voids) { // chiamo doInBackground() senza parametri
        return new MeteoFetcher().fetchItems();
    }
    @Override
    protected void onPostExecute(List<String> items) {
        listener.onTaskCompleted(items); // alla fine del Task richiamo il metodo onTaskCompleted() del listener
    }
}