package ch.supsi.dti.isin.meteoapp.model;

import java.util.UUID;

public class Location {
    public UUID Id;
    public String mName;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Location() {
        Id = UUID.randomUUID();
    }
    public Location(UUID Id, String mName) {
        this.Id = Id;
        this.mName = mName;
    }

    @Override
    public String toString() {
        return "Name: " + mName + "  Id: " + Id;
    }
}