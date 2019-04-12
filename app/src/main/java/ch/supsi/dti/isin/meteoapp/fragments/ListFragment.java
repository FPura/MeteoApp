package ch.supsi.dti.isin.meteoapp.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.db.DatabaseHelper;
import ch.supsi.dti.isin.meteoapp.db.DbSchema;
import ch.supsi.dti.isin.meteoapp.db.LocationsContentValues;
import ch.supsi.dti.isin.meteoapp.db.LocationsCursorWrapper;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.tasks.OnTaskCompletedLocations;
import ch.supsi.dti.isin.meteoapp.tasks.WeatherTask;
import ch.supsi.dti.isin.meteoapp.tasks.WeathersTask;

public class ListFragment extends Fragment implements OnTaskCompletedLocations {
    private static LocationAdapter adapter;
    private SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activate options menu
        setHasOptionsMenu(true);

        // Create and/or open a database that will be used for reading and writing.
        database = new DatabaseHelper(getActivity()).getWritableDatabase();

        // Read locations from DB and load it in LocationHolder
        readDataFromDB();

        // Update Locations weather
        updateAll();
    }

    // Update all the location weather
    private void updateAll() {

        // Get locations
        List<Location> locations = LocationsHolder.get().getLocations();

        // Get locations listener
        LocationsListener locationsListener = new LocationsListener();

        // Start background task who update the view
        new WeathersTask(locationsListener, locations).execute();
    }

    // Notify the adapter all task was completed
    private class LocationsListener implements OnTaskCompletedLocations {

        // Notify when data is changed
        @Override
        public void onTaskCompleted(Location location) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onTaskCompletedCoordinate(Location location) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView locationRecyclerView = view.findViewById(R.id.recycler_view);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Location> locations = LocationsHolder.get().getLocations();
        adapter = new LocationAdapter(locations);
        locationRecyclerView.setAdapter(adapter);

        return view;
    }

    // Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Add menu fragment
        inflater.inflate(R.menu.fragment_list, menu);
    }

    // Menu button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If pushed add button
            case R.id.menu_add:
                addNewLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Start a dialog for add new location
    public void addNewLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("City");

        final EditText input = new EditText(getActivity());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // If push the OK button, we process the city and close the dialog
        builder.setPositiveButton("OK", (dialog, which) -> {

            // Create a new Location
            Location newLocation = new Location();
            newLocation.setName(input.getText().toString().toLowerCase());

            // Start a new background task, who communicate white the API
            WeatherTask weatherTask = new WeatherTask(ListFragment.this, newLocation);
            weatherTask.execute();
        });

        // If push the cancel button we close the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }


    boolean checkIfIsNotDuplicate(Location location) {
        // Get locations
        List<Location> locations = LocationsHolder.get().getLocations();

        // Check if exists location
        return !locations.stream().anyMatch(l -> l.getName().equals(location.getName()));
    }

    // Update the view on task completed
    @Override
    public void onTaskCompleted(Location newLocation) {

        // Check the weather is not null and there is not a duplicate
        if (newLocation != null && newLocation.getWeather() != null && checkIfIsNotDuplicate(newLocation)) {

            // We add the location in LocationHolder
            LocationsHolder.addLocation(newLocation);

            // Notify the change
            adapter.notifyDataSetChanged();

            // Insert the location in DB
            insertData(newLocation);
        }
    }

    // Update the view on task completed
    @Override
    public void onTaskCompletedCoordinate(Location update) {

        // Check the weather is not null
        if (update.getWeather() != null) {

            // Check if is not duplicate
            if (checkIfIsNotDuplicate(update)) {

                // Add it on LocationsHolder
                LocationsHolder.addLocation(update);

                // We update the view
                adapter.notifyDataSetChanged();

                // If name is changed
            } else if (update.isNameChanged()) {

                // Update the view
                adapter.notifyDataSetChanged();
            }
        }
    }

    // Holder
    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView nameTextView;
        private TextView tempTextView;
        private ImageView imageView;
        private Location location;

        LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));

            // Set Listener
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            // Set view
            nameTextView = itemView.findViewById(R.id.city_name);
            tempTextView = itemView.findViewById(R.id.temp);
            imageView = itemView.findViewById(R.id.previewWeather);
        }

        // Start detail activity
        @Override
        public void onClick(View view) {

            // Return an intent who contain location id and activity
            Intent intent = DetailActivity.newIntent(getActivity(), location.getId());

            // Start activity
            startActivity(intent);
        }

        // Delete the location
        @Override
        public boolean onLongClick(View view) {

            // We can't delete the current location
            if (!location.isCurrentLocation()) {

                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {

                        // Yes button is pushed, we delete the location
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteData(location.getName());
                            break;

                        // Another view is pushed
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };

                // Start dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // We ask if is sure to delete the location
                builder.setMessage("Are you sure you want to delete this location?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

            // we have to return a value
            return true;
        }

        // Bind location to LocationHolder
        void bind(Location location) {
            // Set the view name
            nameTextView.setText(location.getName());

            // If location is current location, we change the text color to red otherwise is black
            if (location.isCurrentLocation())
                nameTextView.setTextColor(Color.RED);
            else
                nameTextView.setTextColor(Color.BLACK);

            // If weather is not null we set the temperature and image
            if (location.getWeather() != null) {
                imageView.setImageBitmap(location.getWeather().getBitmap());
                String text = (int) (location.getWeather().getTemperature() - 273.15) + " °C";
                tempTextView.setText(text);
            }

            // Set location
            this.location = location;
        }
    }

    // Adapter
    public class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> locations;

        // Set the locations
        LocationAdapter(List<Location> locations) {
            this.locations = locations;
        }

        // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
        @NonNull
        @Override
        public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // Obtains the LayoutInflater from the activity.
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            // Return the LocationHolder
            return new LocationHolder(layoutInflater, parent);
        }

        // Bind Location to LocationHolder
        @Override
        public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
            // Get the location
            Location location = locations.get(position);

            //Bind the location to LocationHolder
            holder.bind(location);
        }

        // Get the size of locations
        @Override
        public int getItemCount() {
            return locations.size();
        }

    }

    // Delete location from the DB
    private void deleteData(final String cityName) {
        // Remove it from the list
        if (LocationsHolder.delete(cityName)) {
            // Delete it from DB
            database.delete(DbSchema.Table.NAME, "name" + " = \"" + cityName + "\" ;", null);

            // Notify the change on view
            adapter.notifyDataSetChanged();
        }
    }

    // Inset Location in to DB
    private void insertData(Location location) {

        // Create a DB values with location
        ContentValues values = LocationsContentValues.getContentValues(location);

        // Insert location in the DB
        database.insert(DbSchema.Table.NAME, null, values);
    }


    private void readDataFromDB() {
        // List of cities
        List<Location> locations = new ArrayList<>();

        // DB cursor
        Cursor DBCursor = null;

        // Wrapper for the DB cursor
        LocationsCursorWrapper cursor = null;

        try {
            // Get DB cursor instance
            DBCursor = database.query(DbSchema.Table.NAME, null, null, null, null, null, null);

            // Get the wrapper
            cursor = new LocationsCursorWrapper(DBCursor);

            // Set at start of DB
            cursor.moveToFirst();

            // Add the cities
            for (; !cursor.isAfterLast(); cursor.moveToNext())
                locations.add(cursor.getEntry());

        } catch (Exception e) {

            // Return an empty locations list if something gone wrong
            locations = new ArrayList<>();

        } finally {
            // Close cursor
            if (cursor != null)
                cursor.close();

            // Close the DB
            if (DBCursor != null)
                DBCursor.close();

            // Set the locations
            LocationsHolder.setLocations(locations);
        }
    }

    // Return the database instance
    public SQLiteDatabase getDB() {
        return database;
    }

    // Set the database instance
    public void setDB(SQLiteDatabase mDatabase) {
        this.database = mDatabase;
    }
}