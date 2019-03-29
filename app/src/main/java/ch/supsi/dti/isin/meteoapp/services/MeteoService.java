package ch.supsi.dti.isin.meteoapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ch.supsi.dti.isin.meteoapp.R;

public class MeteoService extends IntentService {

    private static final String TAG = "MeteoService";
    private static PendingIntent pi;

    public MeteoService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        // creo l'intent e lo impacchetto in un PendingIntent
        Intent i = MeteoService.newIntent(context);
        pi = PendingIntent.getService(context, 0, i, 0);
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
        Log.i("MSC","MeteoService called. Intent: " + intent);
        if(aCityIsUnderZeroDegrees())
            sendNotification("Brr...","It's very cold...");
    }

    private boolean aCityIsUnderZeroDegrees() {
        return true;
    }

    private void sendNotification(String title, String message) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Meteo Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Meteo Service Channel to send notifications on temperature updates (whean a temperature goes under 0).");
            mNotificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.snow)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotificationManager.notify(0, mBuilder.build());
    }


}
