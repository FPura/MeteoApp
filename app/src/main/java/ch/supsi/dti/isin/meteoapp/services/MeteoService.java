package ch.supsi.dti.isin.meteoapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
/*
public class MeteoService extends IntentService {

    public static void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent
        Intent i = MeteoService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    60000, pi); // 60'000 ms = 1 minuto
        else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MeteoService.class);
    }
    @Override
    protected void onHandleIntent(Intent intent) { // metodo che risponde agli Intent
        Log.i("intent","Received an intent: " + intent);
    }


}*/
