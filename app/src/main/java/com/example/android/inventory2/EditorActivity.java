package com.example.android.inventory2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory2.data.VeggieContract.VeggieEntry;
import com.example.android.inventory2.data.VeggieDbHelper;

/**
 * Allows user to create a new vegetable or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the veggie data loader
     */
    private static final int EXISTING_VEGGIE_LOADER = 0;

    /**
     * Content URI for the existing veggie (null if it's a new veggie)
     */
    private Uri mCurrentVeggieUri;

    /**
     * EditText field to enter the vegetable's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the vegetable's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the vegetable's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier's phone number
     */
    private EditText mSupplierPhoneEditText;

    /**
     * Boolean flag that keeps track of whether the veggie has been edited (true) or not (false)
     */
    private boolean mVeggieHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mVeggieHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentVeggieUri = intent.getData();

        // If the intent DOES NOT contain a veggie content URI, then we know that we are
        // creating a new veggie.
        if (mCurrentVeggieUri == null) {
            // This is a new veggie, so change the app bar to say "Add a vegetable"
            setTitle(getString(R.string.editor_activity_title_new_veggie));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a veggie that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing veggie, so change app bar to say "Edit vegetable"
            setTitle(getString(R.string.editor_activity_title_edit_veggie));

            // Initialize a loader to read the veggie data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_VEGGIE_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_veggie_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_veggie_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_veggie_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Get user input from editor and save veggie into database.
     */
    private void saveVeggie() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();


        // Check if this is supposed to be a new veggie
        // and check if all the fields in the editor are blank
        if (mCurrentVeggieUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString)
                && TextUtils.isEmpty(supplierPhoneString)) {
            // Since no fields were modified, we can return early without creating a new veggie.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and veggie attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(VeggieEntry.COLUMN_VEGGIE_NAME, nameString);
        values.put(VeggieEntry.COLUMN_VEGGIE_PRICE, priceString);
        values.put(VeggieEntry.COLUMN_VEGGIE_QUANTITY, quantityString);
        values.put(VeggieEntry.COLUMN_VEGGIE_SUPPLIER_NAME, supplierNameString);
        values.put(VeggieEntry.COLUMN_VEGGIE_SUPPLIER_PHONE, supplierPhoneString);

        // Show a toast message when the veggie's name is missing.
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.required_name), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a toast message when the veggie's price is missing.
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.required_price), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a toast message when the veggie's quantity is missing.
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.required_quantity), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a toast message when the supplier's name is missing.
        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.required_supplier), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a toast message when the supplier's phone number is missing.
        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.required_supplier_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a toast message when the user enters a negative price.
        int priceInt = Integer.parseInt(priceString);
        if (priceInt < 0) {
            mPriceEditText.setError(getString(R.string.negative_price));
            Toast.makeText(this, getString(R.string.negative_price), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a toast message when the user enters a negative quantity.
        int quantityInt = Integer.parseInt(quantityString);
        if (quantityInt < 0) {
            mQuantityEditText.setError(getString(R.string.negative_quantity));
            Toast.makeText(this, getString(R.string.negative_quantity), Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine if this is a new or existing veggie by checking if mCurrentVeggieUri is null or not
        if (mCurrentVeggieUri == null) {
            // This is a NEW veggie, so insert a new veggie into the provider,
            // returning the content URI for the new veggie.
            Uri newUri = getContentResolver().insert(VeggieEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_veggie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_veggie_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING veggie, so update the veggie with content URI: mCurrentVeggieUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentVeggieUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentVeggieUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_veggie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_veggie_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new veggie, hide the "Delete" menu item.
        if (mCurrentVeggieUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save vegetable to database
                saveVeggie();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the veggie hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mVeggieHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the veggie hasn't changed, continue with handling back button press
        if (!mVeggieHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all veggie attributes, define a projection that contains
        // all columns from the veggie table
        String[] projection = {
                VeggieEntry._ID,
                VeggieEntry.COLUMN_VEGGIE_NAME,
                VeggieEntry.COLUMN_VEGGIE_PRICE,
                VeggieEntry.COLUMN_VEGGIE_QUANTITY,
                VeggieEntry.COLUMN_VEGGIE_SUPPLIER_NAME,
                VeggieEntry.COLUMN_VEGGIE_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentVeggieUri,         // Query the content URI for the current veggie
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of veggie attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_NAME);
            int priceColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_SUPPLIER_NAME);
            final int phoneColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplier);
            mSupplierPhoneEditText.setText(phone);

            // Increase the quantity when the "+" button is clicked.
            Button buttonIncrease = findViewById(R.id.action_increase_quantity);
            buttonIncrease.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String quantityString = mQuantityEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(quantityString)) {
                        int increasedQuantity = Integer.parseInt(quantityString);
                        increasedQuantity++;
                        mQuantityEditText.setText("" + increasedQuantity);
                    }
                }
            });

            // Decrease the quantity when the "-" button is clicked.
            Button buttonDecrease = findViewById(R.id.action_decrease_quantity);
            buttonDecrease.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String quantityString = mQuantityEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(quantityString)) {
                        int decreasedQuantity = Integer.parseInt(quantityString);
                        if (decreasedQuantity <= 0) {
                            // Show a toast message that quantity cannot be less than 0
                            Toast.makeText(getApplicationContext(), getString(R.string.negative_quantity), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        decreasedQuantity--;
                        mQuantityEditText.setText("" + decreasedQuantity);
                    }
                }
            });

            // Dial the supplier when the "Call Supplier" button is clicked.
            Button button = findViewById(R.id.action_call_supplier);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString()));
                    startActivity(callIntent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this veggie.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the veggie.
                deleteVeggie();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the veggie.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the veggie in the database.
     */
    private void deleteVeggie() {
        // Only perform the delete if this is an existing veggie.
        if (mCurrentVeggieUri != null) {
            // Call the ContentResolver to delete the veggie at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the veggie that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentVeggieUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_veggie_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_veggie_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}