package com.example.android.inventory2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory2.data.VeggieContract.VeggieEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class VeggieDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = VeggieDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "veggies.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link VeggieDbHelper}.
     *
     * @param context of the app
     */
    public VeggieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the veggies table
        String SQL_CREATE_VEGGIES_TABLE = "CREATE TABLE " + VeggieEntry.TABLE_NAME + " ("
                + VeggieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VeggieEntry.COLUMN_VEGGIE_NAME + " TEXT NOT NULL, "
                + VeggieEntry.COLUMN_VEGGIE_PRICE + " REAL NOT NULL, "
                + VeggieEntry.COLUMN_VEGGIE_QUANTITY + " INTEGER NOT NULL, "
                + VeggieEntry.COLUMN_VEGGIE_SUPPLIER_NAME + " TEXT NOT NULL, "
                + VeggieEntry.COLUMN_VEGGIE_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_VEGGIES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}