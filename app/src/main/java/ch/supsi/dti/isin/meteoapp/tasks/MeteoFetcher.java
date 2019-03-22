package ch.supsi.dti.isin.meteoapp.tasks;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.supsi.dti.isin.meteoapp.jsonparse.JSONWeatherParser;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.Weather;

public class MeteoFetcher {

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // cast a HttpURLConnection
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream(); // apro la connessione
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) { // leggo finché ci sono dati
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException { // versione del metodo che torna una stringa
        return new String(getUrlBytes(urlSpec));
    }

    //TODO: implement Meteo API fetch
    public Weather fetchItems(String apikey, String cityname){
        try{
            String url = Uri.parse("https://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("q",cityname)
                    .appendQueryParameter("APPID",apikey)
                    .build()
                    .toString();

            String jsonString = getUrlString(url);

            return JSONWeatherParser.parse(jsonString);
        }catch (Exception e){ Log.e("Exceptions",e.getMessage());}

        return null;
    }
}
