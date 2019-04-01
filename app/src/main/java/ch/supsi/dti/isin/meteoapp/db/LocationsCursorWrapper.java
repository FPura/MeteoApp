package ch.supsi.dti.isin.meteoapp.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.model.Location;

public class LocationsCursorWrapper extends CursorWrapper {
    public LocationsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    // Return row of the table
    public Location getEntry() {
        String id = getString(getColumnIndex(DbSchema.Table.Cols.UUID));
        String name = getString(getColumnIndex(DbSchema.Table.Cols.NAME));
        return new Location(UUID.fromString(id), name);
    }
}