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
        sendNotification("It's very cold...");
    }

    private void sendNotification(String message) {
        // in Android >= 8.0 devo registrare il canale delle notifiche a livello di sistema (prossima slide)
        // creo il contenuto della notifica
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.snow)
                .setContentTitle("Brr...")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(pi);

        // creo il NotificationChannel, solo in caso di API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MNC", "MeteoNotificationChannel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canale di notifiche per la temperatura");

            // registro il canale a livello di sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0, mBuilder.build());
            Log.i("MSC","Notification sent");
        } else {
            NotificationManager mNotificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }


}
