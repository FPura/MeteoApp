package ch.supsi.dti.isin.meteoapp.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import ch.supsi.dti.isin.meteoapp.db.DatabaseHelper;
import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import ch.supsi.dti.isin.meteoapp.tasks.WeatherTaskCoordinate;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainActivity extends SingleFragmentActivity {

    // Request granted code
    private static final int REQ_CODE = 100;

    //TAG
    private final String PERMISSION_REQUEST_TAG = "Permission request";
    private final String PERMISSION_STATUS_TAG = "Permission status";

    // List fragment (contains logic)
    private ListFragment listFragment;


    // Flag indicating whether the notification was sent.
    private boolean sendedNotification = false;

    // Current Location
    // TODO: vedere se vogliamo mantenerlo cosi o cambiarli il nome cosi da poterlo importare e istanziare normalemente
    private ch.supsi.dti.isin.meteoapp.model.Location currentLocation = null;

    @Override
    protected Fragment createFragment() {
        /* Check if that has the permission
         * PERMISSION_DENIED  == -1
         * PERMISSION_GRANTED ==  0
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(PERMISSION_REQUEST_TAG, "DENIED");

            // Requires permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
        } else {

            Log.i(PERMISSION_REQUEST_TAG, "GRANTED");

            // Start tracking the position
            startLocationListener();
        }

        listFragment = new ListFragment();

        return listFragment;
    }

    private void startLocationListener() {
        /* LocationParams(LocationAccuracy accuracy, long interval, float distance)
         * https://github.com/mrmans0n/smart-location-lib/blob/master/library/src/main/java/io/nlopez/smartlocation/location/config/LocationParams.java
         */
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(0)
                .setInterval(60000); // 1 min

        /*
         * https://github.com/mrmans0n/smart-location-lib/blob/master/library/src/main/java/io/nlopez/smartlocation/SmartLocation.java
         */
        SmartLocation.with(this).location().continuous().config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        // Check currentLocation is not null
                        if (currentLocation == null)
                            currentLocation = new ch.supsi.dti.isin.meteoapp.model.Location();

                        // Check it has the weather
                        if (currentLocation.getWeather() != null) {

                            // Get current temperature
                            double currentTemperature = currentLocation.getWeather().getTemperature();

                            // Check the notification is not sent and the temperature is lower than 3 °G
                            if (!sendedNotification && currentTemperature <= 276.15) {
                                sendNotification(currentLocation);
                                sendedNotification = true;
                            } // Check the notification is sent and the temperature is greater than 3 °G
                            else if (sendedNotification && currentTemperature > 276.15) {
                                sendedNotification = false;
                            }
                        }

                        // Set Long and Lat of current location
                        currentLocation.setLongitude(location.getLongitude());
                        currentLocation.setLatitude(location.getLatitude());

                        // Start background task
                        WeatherTaskCoordinate weatherTask = new WeatherTaskCoordinate(listFragment, currentLocation);
                        weatherTask.execute();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE: {

                // Check App has the permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // PERMISSION_GRANTED
                    Log.i(PERMISSION_STATUS_TAG, "GRANTED");

                    // Start reading the position every minute
                    startLocationListener();
                }
                break;
            }
        }
    }

    // Send the notification to user
    private void sendNotification(ch.supsi.dti.isin.meteoapp.model.Location location) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("default", "TEST_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Test Channel Description");
        mNotificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Alert low temperature!")
                .setContentText("Location: " + location.getName() + " - temp: " + location.getWeather().getTemperature() + " °K")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Check DB is not null
        if (listFragment.getDB() != null)
            // Close the DB
            listFragment.getDB().close();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check listFragment is not null
        if (listFragment == null)
            listFragment = new ListFragment();

        // Set the DB on listFragment
        listFragment.setDB(new DatabaseHelper(this).getWritableDatabase());
    }
}