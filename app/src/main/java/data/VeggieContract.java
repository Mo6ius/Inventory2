package com.example.android.inventory2.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory2 app.
 */
public final class VeggieContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private VeggieContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory2";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.inventory2/inventory2/ is a valid path for
     * looking at inventory2 data. content://com.example.android.inventory2/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_VEGGIES = "veggies";

    /**
     * Inner class that defines constant values for the vegetables database table.
     * Each entry in the table represents a single vegetable.
     */
    public static final class VeggieEntry implements BaseColumns {

        /**
         * The content URI to access the veggie data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEGGIES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of veggies.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEGGIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single veggie.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEGGIES;

        /**
         * Name of database table for vegetables
         */
        public final static String TABLE_NAME = "veggies";

        /**
         * Unique ID number for the vegetable (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the vegetable.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_VEGGIE_NAME = "name";

        /**
         * Price of the vegetable.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_VEGGIE_PRICE = "price";

        /**
         * Quantity of the vegetable.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_VEGGIE_QUANTITY = "quantity";

        /**
         * Name of supplier
         * <p>
         * Type: STRING
         */
        public final static String COLUMN_VEGGIE_SUPPLIER_NAME = "supplierName";

        /**
         * Phone of supplier
         * <p>
         * Type: STRING
         */
        public final static String COLUMN_VEGGIE_SUPPLIER_PHONE = "supplierPhone";

    }

}