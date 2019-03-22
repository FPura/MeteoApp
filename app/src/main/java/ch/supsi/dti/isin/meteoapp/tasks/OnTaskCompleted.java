package ch.supsi.dti.isin.meteoapp.tasks;

import ch.supsi.dti.isin.meteoapp.model.Weather;

public interface OnTaskCompleted {
    void onTaskCompleted(Weather weather);
}
