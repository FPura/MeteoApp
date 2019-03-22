package ch.supsi.dti.isin.meteoapp.tasks;

import java.util.List;

public interface OnTaskCompleted {
    void onTaskCompleted(List<String> items);
}
