package ch.supsi.dti.isin.meteoapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;

public class DetailLocationFragment extends Fragment {

    // Bundle key for location values
    private static final String ARG_LOCATION_ID = "location_id";

    // Location
    private Location location;

    public static DetailLocationFragment newInstance(UUID locationId) {

        // Save Location id for the DetailLocationFragment
        Bundle bundleArgument = new Bundle();
        bundleArgument.putSerializable(ARG_LOCATION_ID, locationId);

        // Crate DetailLocationFragment and put inside the arguments
        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(bundleArgument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( getArguments() != null){
            // Get the location UUID from Bundle
            UUID locationId = (UUID) getArguments().getSerializable(ARG_LOCATION_ID);

            // Get the location from LocationHolder
            location = LocationsHolder.get().getLocation(locationId);
        }
    }

    //  Creates and returns the view hierarchy associated with the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get a View from the fragment_detail_location file
        View view = inflater.inflate(R.layout.fragment_detail_location, container, false);

        // View which contain the cityName
        TextView cityNameView = view.findViewById(R.id.city_name);
        TextView tempView = view.findViewById(R.id.temp);
        ImageView imageView = view.findViewById(R.id.weather_image);

        // Set on View the name of the city
        cityNameView.setText(location.getName());

        // Set on View the temperature
        tempView.setText(((int) (location.getWeather().getTemperature()-273.15)) + " °C");

        // Set on View the image
        imageView.setImageBitmap(location.getWeather().getBitmap());

        return view;
    }
}