package com.example.android.inventory2;

        import android.content.ContentUris;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.CursorAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.android.inventory2.data.VeggieContract.VeggieEntry;

/**
 * {@link VeggieCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of veggie data as its data source. This adapter knows
 * how to create list items for each row of veggie data in the {@link Cursor}.
 */
public class VeggieCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link VeggieCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public VeggieCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the veggie data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current veggie can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of veggie attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_NAME);
        int priceColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(VeggieEntry.COLUMN_VEGGIE_QUANTITY);

        // Read the veggie attributes from the Cursor for the current veggie
        String veggieName = cursor.getString(nameColumnIndex);
        String veggiePrice = cursor.getString(priceColumnIndex);
        int veggieQuantity = cursor.getInt(quantityColumnIndex);

        // Update the TextViews with the attributes for the current veggie
        nameTextView.setText(veggieName);
        priceTextView.setText(veggiePrice);
        quantityTextView.setText(Integer.toString(veggieQuantity));

        // column number of "_ID"
        int veggieIDColumnIndex = cursor.getColumnIndex(VeggieEntry._ID);

        // Read the veggie attributes from the Cursor for the current veggie for "Sale" button
        final long veggieID = Integer.parseInt(cursor.getString(veggieIDColumnIndex));
        final int currentQuantity = cursor.getInt(quantityColumnIndex);

        /*
         * Each list view item has a "Sale" button
         * This "Sale" button has OnClickListener which will decrease the product quantity by one at a time.
         * This function only executes if the quantity is greater than 0.
         */
        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri currentUri = ContentUris.withAppendedId(VeggieEntry.CONTENT_URI, veggieID);

                int updatedQuantity = (currentQuantity - 1);

                if ((updatedQuantity) >= 0) {
                    ContentValues values = new ContentValues();
                    values.put(VeggieEntry.COLUMN_VEGGIE_QUANTITY, updatedQuantity);
                    context.getContentResolver().update(currentUri, values, null, null);
                } else {
                    // Show a toast message that quantity cannot be less than 0
                    Toast.makeText(view.getContext(), (R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}