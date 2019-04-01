package ch.supsi.dti.isin.meteoapp.jsonparse;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Objects;

import ch.supsi.dti.isin.meteoapp.model.Weather;

public class JSONWeatherParser {

    private final static String iconAPI = "http://openweathermap.org/img/w/";

    public static Weather parse(final String json) {
        Weather weather = new Weather();
        try {
            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(json);

            // "weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}]
            JSONObject weatherInfo = (JSONObject) ((JSONArray) Objects.requireNonNull(resultObject.get("weather"))).get(0);

            weather.setTemperature((double) ((JSONObject) Objects.requireNonNull(resultObject.get("main"))).get("temp"));
            weather.setDescription((String) weatherInfo.get("description"));
            weather.setMain((String) weatherInfo.get("main"));
            weather.setWeatherResourceImage(iconAPI + weatherInfo.get("icon") + ".png");
            weather.setLocationName((String) resultObject.get("name"));

        } catch (ParseException e) {
            Log.e("ERROR", e.toString());
        }
        return weather;
    }

    public static String getCityName(final String json) {
        String cityName = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(json);
            //{"coord":{"lon":9.19,"lat":45.47},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"base":"stations","main":{"temp":283.86,"pressure":1015,"humidity":50,"temp_min":279.82,"temp_max":288.71},"visibility":10000,"wind":{"speed":1},"clouds":{"all":0},"dt":1554068446,"sys":{"type":1,"id":6742,"message":0.0057,"country":"IT","sunrise":1554008770,"sunset":1554054516},"id":3173435,"name":"Milano","cod":200}
            cityName = resultObject.get("name").toString();
        } catch (ParseException e) {
            Log.e("ERROR", e.toString());
        }
        return cityName;
    }
}
