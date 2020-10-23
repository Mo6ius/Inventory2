package com.example.android.inventory2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory2.EditorActivity;
import com.example.android.inventory2.R;
import com.example.android.inventory2.data.VeggieContract.VeggieEntry;
import com.example.android.inventory2.data.VeggieDbHelper;

/**
 * Displays list of veggies that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the veggie data loader
     */
    private static final int VEGGIE_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    VeggieCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the veggie data
        ListView veggieListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        veggieListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no veggie data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new VeggieCursorAdapter(this, null);
        veggieListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        veggieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific veggie that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link VeggieEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.veggies/veggies/2"
                // if the veggie with ID 2 was clicked on.
                Uri currentVeggieUri = ContentUris.withAppendedId(VeggieEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentVeggieUri);

                // Launch the {@link EditorActivity} to display the data for the current veggie.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(VEGGIE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded veggie data into the database. For debugging purposes only.
     */
    private void insertVeggie() {

        // Create a ContentValues object where column names are the keys,
        // and carrot's veggies attributes are the values.
        ContentValues values = new ContentValues();
        values.put(VeggieEntry.COLUMN_VEGGIE_NAME, "Carrot");
        values.put(VeggieEntry.COLUMN_VEGGIE_PRICE, "4");
        values.put(VeggieEntry.COLUMN_VEGGIE_QUANTITY, 3);
        values.put(VeggieEntry.COLUMN_VEGGIE_SUPPLIER_NAME, "Whole Foods");
        values.put(VeggieEntry.COLUMN_VEGGIE_SUPPLIER_PHONE, "(800) 123-4567");

        // Insert a new row for carrot into the provider using the ContentResolver.
        // Use the {@link VeggieEntry#CONTENT_URI} to indicate that we want to insert
        // into the veggies database table.
        // Receive the new content URI that will allow us to access carrot's data in the future.
        Uri newUri = getContentResolver().insert(VeggieEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all veggies in the database.
     */
    private void deleteAllVeggies() {
        int rowsDeleted = getContentResolver().delete(VeggieEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from veggie database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertVeggie();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllVeggies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                VeggieEntry._ID,
                VeggieEntry.COLUMN_VEGGIE_NAME,
                VeggieEntry.COLUMN_VEGGIE_PRICE,
                VeggieEntry.COLUMN_VEGGIE_QUANTITY,
                VeggieEntry.COLUMN_VEGGIE_SUPPLIER_NAME,
                VeggieEntry.COLUMN_VEGGIE_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                VeggieEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link VeggieCursorAdapter} with this new cursor containing updated veggie data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}