package ch.supsi.dti.isin.meteoapp.db;

import android.content.ContentValues;

import ch.supsi.dti.isin.meteoapp.model.Location;

public class LocationsContentValues {

    // add the value of table
    public static ContentValues getContentValues(final Location entry) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.Table.Cols.UUID, entry.getId().toString());
        values.put(DbSchema.Table.Cols.NAME, entry.getName());
        return values;
    }
}