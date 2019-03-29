package ch.supsi.dti.isin.meteoapp.tasks;

import ch.supsi.dti.isin.meteoapp.model.Location;

public interface OnTaskCompleted {
    void onTaskCompleted(Location newLocation);
}
