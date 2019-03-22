package ch.supsi.dti.isin.meteoapp.jsonparse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

import ch.supsi.dti.isin.meteoapp.model.Weather;

public class JSONWeatherParser {

    private final static String iconAPI = "http://openweathermap.org/img/w/";
    public static Weather parse (String json){
        Weather weather = new Weather();
        try {
            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(json);

            JSONObject weatherInfo = (JSONObject)((JSONArray) resultObject.get("weather")).get(0);
            weather.setTemperature((float)((JSONObject) resultObject.get("main")).get("temp"));
            weather.setDescription((String) weatherInfo.get("description"));
            weather.setMain((String) weatherInfo.get("main"));
            weather.setWeatherResourceImage(iconAPI + weatherInfo.get("icon") + ".png");

            weather.setLocationName((String) resultObject.get("name"));


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weather;
    }
}
