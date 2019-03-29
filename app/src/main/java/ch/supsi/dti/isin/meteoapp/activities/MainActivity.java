package ch.supsi.dti.isin.meteoapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

import ch.supsi.dti.isin.meteoapp.db.DatabaseHelper;
import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import ch.supsi.dti.isin.meteoapp.services.MeteoService;
import ch.supsi.dti.isin.meteoapp.tasks.OnTaskCompleted;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainActivity extends SingleFragmentActivity {

    private static final int REQ_CODE = 100;

    //TAG
    private final String PERMISSION_REQUEST_TAG = "Permission request";
    private final String PERMISSION_STATUS_TAG = "Permission status";
    private ListFragment listFragment;
    private static final String MY_KEY = "key";

    private ch.supsi.dti.isin.meteoapp.model.Location currentLocation = null;

    @Override
    protected Fragment createFragment() {
        /* ContextCompat.checkSelfPermission(Context context, String permission)
         * https://developer.android.com/reference/android/support/v4/content/ContextCompat.html#checkSelfPermission(android.content.Context,%20java.lang.String)
         * PERMISSION_DENIED  == -1
         * PERMISSION_GRANTED ==  0
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(PERMISSION_REQUEST_TAG, "DENIED");

            /* requestPermissions (Activity activity, String[] permissions, int requestCode)
             * https://developer.android.com/reference/android/support/v4/app/ActivityCompat.html#requestPermissions(android.app.Activity,%20java.lang.String[],%20int)
             */
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
        } else {
            Log.i(PERMISSION_REQUEST_TAG, "GRANTED");
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

        /* SmartLocation(Context context, Logger logger, boolean preInitialize)
         * https://github.com/mrmans0n/smart-location-lib/blob/master/library/src/main/java/io/nlopez/smartlocation/SmartLocation.java
         */
        SmartLocation.with(this).location().continuous().config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.i(PERMISSION_STATUS_TAG, " Location: " + location);
                        if(currentLocation == null){
                            currentLocation = new ch.supsi.dti.isin.meteoapp.model.Location();
                        }
                        currentLocation.setLongitude(location.getLongitude());
                        currentLocation.setLatitude(location.getLatitude());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISSION_GRANTED
                    Log.i("Permission status", "GRANTED");
                    startLocationListener();
                }
                break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(listFragment.getDB() != null)
            listFragment.getDB().close();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(listFragment == null){
            listFragment = new ListFragment();
        }
        listFragment.setDB(new DatabaseHelper(this).getWritableDatabase());

        MeteoService.setServiceAlarm(this, true);
    }

    /*
    MeteoTask t = new MeteoTask(MainActivity.this);
    t.execute();
     */

}