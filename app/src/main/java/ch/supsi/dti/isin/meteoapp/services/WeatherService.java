package ch.supsi.dti.isin.meteoapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class WeatherService extends IntentService {

    // Thread name
    private static final String TAG = "TestService";

    // Set the  Thread name
    public WeatherService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent
        Intent intent = WeatherService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    60000, pendingIntent); // 60'000 ms = 1 minuto
        else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static Intent newIntent(final Context context) {
        return new Intent(context, WeatherService.class);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.i("intent","Received an intent: " + intent);
    }
}