package ch.supsi.dti.isin.meteoapp.model;

public class Weather {

    private String weatherResourceImage;
    private double temperature;
    private String description;
    private String locationName;
    private String main;


    public String getWeatherResourceImage() {
        return weatherResourceImage;
    }

    public void setWeatherResourceImage(String weatherResourceImage) {
        this.weatherResourceImage = weatherResourceImage;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
