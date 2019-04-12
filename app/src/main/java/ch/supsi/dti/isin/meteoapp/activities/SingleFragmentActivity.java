package ch.supsi.dti.isin.meteoapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ch.supsi.dti.isin.meteoapp.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    // Abstract method for children
    protected abstract Fragment createFragment();

    // Save the contents
    protected Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set activity content
        setContentView(R.layout.fragment_single_fragment);

        this.savedInstanceState = savedInstanceState;

        // Get the Fragment manager
        FragmentManager fm = getSupportFragmentManager();

        // Check Fragment exists
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        // Check fragment_container is not null
        if (fragment == null) {

            // Check permissions and return a ListFragment instance
            fragment = createFragment();

            // Add fragment_container in FragmentManager
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}