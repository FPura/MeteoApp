package ch.supsi.dti.isin.meteoapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.fragments.DetailLocationFragment;

public class DetailActivity extends SingleFragmentActivity {
    private static final String EXTRA_LOCATION_ID = "ch.supsi.dti.isin.meteoapp.location_id";

    public static Intent newIntent(Context packageContext, UUID locationId) {

        // Create an intent for a specific component.
        Intent intent = new Intent(packageContext, DetailActivity.class);

        // Save the location ID
        intent.putExtra(EXTRA_LOCATION_ID, locationId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        // Get location I
        UUID locationId = (UUID) getIntent().getSerializableExtra(EXTRA_LOCATION_ID);

        // Return a new DetailLocationFragment
        new DetailLocationFragment();
        return DetailLocationFragment.newInstance(locationId);
    }
}