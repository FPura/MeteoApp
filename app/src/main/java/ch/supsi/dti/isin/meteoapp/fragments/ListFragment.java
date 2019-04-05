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
import android.util.Log;
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
        List<Location> locations = LocationsHolder.get().getLocations();
        LocationsListener locationsListener = new LocationsListener();
        new WeathersTask(locationsListener, locations).execute();
    }

    // Notify the adapter all task was completed
    private class LocationsListener implements OnTaskCompletedLocations {
        @Override
        public void onTaskCompleted() {
            adapter.notifyDataSetChanged();
        }

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
        inflater.inflate(R.menu.fragment_list, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                addNewLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addNewLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("City");

        final EditText input = new EditText(getActivity());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Location newLocation = new Location();
                newLocation.setName(input.getText().toString());

                Log.i("APIConnection", "Starting");
                WeatherTask weatherTask = new WeatherTask(ListFragment.this, newLocation);
                weatherTask.execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    boolean checkIfIsDuplicate(Location location) {
        // Get locations
        List<Location> locations = LocationsHolder.get().getLocations();

        // Check if exists location
        return locations.stream().anyMatch(l -> l.getName().equals(location.getName()));
    }

    @Override
    public void onTaskCompleted() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskCompleted(Location newLocation) {
        if (newLocation.getWeather() != null && !checkIfIsDuplicate(newLocation)) {
            LocationsHolder.addLocation(newLocation);
            adapter.notifyDataSetChanged();
            insertData(newLocation);
        }
    }

    @Override
    public void onTaskCompletedCoordinate(Location update) {

        if (update.getWeather() != null) {
            if (!checkIfIsDuplicate(update)) {
                LocationsHolder.addLocation(update);
                adapter.notifyDataSetChanged();
            } else if (update.isNameChanged()) {
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
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            nameTextView = itemView.findViewById(R.id.city_name);
            tempTextView = itemView.findViewById(R.id.temp);
            imageView = itemView.findViewById(R.id.previewWeather);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(), location.getId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view){

            if(!location.isCurrentLocation()) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                deleteData(location.getName());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete this location?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
            return true;
        }

        void bind(Location location) {
            nameTextView.setText(location.getName());
            if(location.isCurrentLocation())
                nameTextView.setTextColor(Color.RED);
            else
                nameTextView.setTextColor(Color.BLACK);
            if (location.getWeather() != null) {
                imageView.setImageBitmap(location.getWeather().getBitmap());
                tempTextView.setText(((int) (location.getWeather().getTemperature()-273.15)) + " °C");
            }
            this.location = location;
        }
    }

    // Adapter
    public class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> locations;

        LocationAdapter(List<Location> locations) {
            this.locations = locations;
        }

        @NonNull
        @Override
        public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
            Location location = locations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

    }

    private void deleteData(final String cityName) {
        // Remove it from the list
        if(LocationsHolder.delete(cityName)) {
            // Delete it from DB
            database.delete(DbSchema.Table.NAME, "name" + " = \"" + cityName + "\" ;", null);
            adapter.notifyDataSetChanged();
        }
    }

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

    public SQLiteDatabase getDB() {
        return database;
    }

    public void setDB(SQLiteDatabase mDatabase) {
        this.database = mDatabase;
    }
}