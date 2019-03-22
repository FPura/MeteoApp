package ch.supsi.dti.isin.meteoapp.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.activities.MainActivity;
import ch.supsi.dti.isin.meteoapp.db.DatabaseHelper;
import ch.supsi.dti.isin.meteoapp.db.DbSchema;
import ch.supsi.dti.isin.meteoapp.db.LocationsContentValues;
import ch.supsi.dti.isin.meteoapp.db.LocationsCursorWrapper;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.tasks.MeteoTask;
import ch.supsi.dti.isin.meteoapp.tasks.OnTaskCompleted;

public class ListFragment extends Fragment implements OnTaskCompleted {
    private RecyclerView mLocationRecyclerView;
    private LocationAdapter mAdapter;
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabase = new DatabaseHelper(getActivity()).getWritableDatabase();
        readData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mLocationRecyclerView = view.findViewById(R.id.recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Location> locations = LocationsHolder.get().getLocations();
        mAdapter = new LocationAdapter(locations);
        mLocationRecyclerView.setAdapter(mAdapter);

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

    //TODO: Collegare le API per verificare che la città esista.
    public void addNewLocation(){
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

               Log.i("APIConnection","Starting");
               MeteoTask meteoTask = new MeteoTask(ListFragment.this);
               meteoTask.execute();
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

    @Override
    public void onTaskCompleted(List<String> items) {
        /*
        LocationsHolder lh = LocationsHolder.get();
        lh.getLocations().add(newLocation);
        insertData(newLocation);
        */
    }


    // Holder
    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTextView;
        private Location mLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
            startActivity(intent);
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
        }
    }

    // Adapter
    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> mLocations;

        public LocationAdapter(List<Location> locations) {
            mLocations = locations;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            Location location = mLocations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }
    }

    private void insertData(Location location){

        ContentValues values = LocationsContentValues.getContentValues(location);
        mDatabase.insert(DbSchema.Table.NAME, null, values);
    }
    private void readData(){

        Cursor c = mDatabase.query(DbSchema.Table.NAME, null, null, null, null, null, null);
        LocationsCursorWrapper cursor = new LocationsCursorWrapper(c);
        List<Location> mLocations = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Location entry = cursor.getEntry();
                mLocations.add(entry);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
            LocationsHolder.addLocations(mLocations);
        }
    }

    public SQLiteDatabase getDB(){
        return mDatabase;
    }
    public void setDB(SQLiteDatabase mDatabase){
        this.mDatabase = mDatabase;
    }
}