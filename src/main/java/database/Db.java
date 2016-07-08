package database;

import base.Database;
import factory.FactorySetup;
import helpers.ReflectionHelper;
import settings.Setup;

public class Db {
    private static final String DIRDB = "database.";
    private static Database database;

    public static Database getInstance() {
        if (database ==null) {
            database = (Database) ReflectionHelper.createInstance(DIRDB + Setup.getInstance().database);
            FactorySetup.addToFactory("Database",database);
        }
        return database;
    }

    private Db() {
    }
}

