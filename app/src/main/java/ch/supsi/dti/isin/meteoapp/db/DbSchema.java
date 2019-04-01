package ch.supsi.dti.isin.meteoapp.db;

public class DbSchema {
    public static final class Table {

        // Table  name
        public static final String NAME = "locations";

        static final class Cols {

            // Unique user Identifier
            static final String UUID = "uuid";

            // City name
            static final String NAME = "name";
        }
    }
}