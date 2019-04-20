package ch.supsi.dti.isin.meteoapp.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.supsi.dti.isin.meteoapp.jsonparse.JSONWeatherParser;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.Weather;

class WeatherFetch {
    // API KEY
    private final String apiKey = "815504bb440299e3ebbb76868cbc7c47";

    // Read data frm URL and return it
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Output data
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Open connection
        InputStream input = connection.getInputStream();

        try {
            // Check the connection
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

            int bytesRead;
            byte[] buffer = new byte[1024];

            // Read data until they are finished
            while ((bytesRead = input.read(buffer)) > 0)
                output.write(buffer, 0, bytesRead);

            return output.toByteArray();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            return null;
        } finally {
            input.close();
            output.close();
            connection.disconnect();
        }
    }

    // Return the URL in string format
    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    // Return a bitmap from an url (return the icon of the weather)
    private Bitmap getBitmapFromURL(String src) {
        try {
            byte[] readBytes = getUrlBytes(src);
            if (readBytes != null)
                return BitmapFactory.decodeByteArray(readBytes, 0, readBytes.length);
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Makes an URL request and return a weather object.
    Weather fetchItems(final String cityName) {
        try {
            // Composes the URL
            String url = Uri.parse("https://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("q", cityName)
                    .appendQueryParameter("APPID", apiKey)
                    .build()
                    .toString();

            // Make the request and return the data
            String jsonString = getUrlString(url);

            // Parse the string and return the data
            Weather weather = JSONWeatherParser.parse(jsonString);

            // Create the bitmap to get the icon
            weather.setBitmap(getBitmapFromURL(weather.getWeatherResourceImage()));
            return weather;
        } catch (Exception e) {
            Log.e("Exceptions", e.getMessage());
        }
        return null;
    }

    void fetchItemsCoordinate(double longitude, double latitude, Location location) {
        try {
            // Composes the URL
            String url = Uri.parse("https://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("lon", Double.toString(longitude))
                    .appendQueryParameter("lat", Double.toString(latitude))
                    .appendQueryParameter("APPID", apiKey)
                    .build()
                    .toString();

            // Make the request and return the data
            String jsonString = getUrlString(url);

            // Get the city name by weather services
            String cityNameBYCoordinate = JSONWeatherParser.getCityName(jsonString);

            // If name is changed
            if (location.getName() == null || location.getName().compareTo(cityNameBYCoordinate) != 0) {

                // Parse the string and return the data
                Weather weather = JSONWeatherParser.parse(jsonString);

                // Set new city name
                location.setName(cityNameBYCoordinate);

                // Create the bitmap to get the icon
                weather.setBitmap(getBitmapFromURL(weather.getWeatherResourceImage()));

                // Set new weather
                location.setWeather(weather);

                // Set nameChanged to true
                location.setNameChanged(true);
            }
        } catch (Exception e) {
            Log.e("Exceptions", e.getMessage());
        }
    }
}