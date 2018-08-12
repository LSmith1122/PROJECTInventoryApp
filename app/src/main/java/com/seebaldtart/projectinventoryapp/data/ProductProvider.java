package com.seebaldtart.projectinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.R;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

public class ProductProvider extends ContentProvider {
    public static final int PRODUCTS = 100;
    public static final int PRODUCTS_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS_ID, PRODUCTS_ID);
    }

    private final String LOG_TAG = ProductProvider.class.getSimpleName();
    private final int BAD_ID = -1;
    private final int one = 1;
    private final int zero = 0;
    private ProductDBHelper mDBHelper;
    private Context context;

    @Override
    public boolean onCreate() {
        mDBHelper = new ProductDBHelper(getContext());
        context = getContext();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = mDBHelper.getReadableDatabase();
            Log.i("TEST", "URI: " + uri);
            switch (sUriMatcher.match(uri)) {
                case PRODUCTS:
                    Log.i("TEST", "match: " + sUriMatcher.match(uri));
                    cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case PRODUCTS_ID:
                    Log.i("TEST", "match: " + sUriMatcher.match(uri));
                    selection = BookEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException(context.getResources().getString(R.string.illegal_argument_exception_invalid_uri));
            }
        } catch (NullPointerException e) {
            Toast.makeText(context, context.getResources().getString(R.string.error_querying_database), Toast.LENGTH_SHORT).show();
        }
        if (cursor == null) {
            return null;
        } else {
            cursor.setNotificationUri(context.getContentResolver(), uri);       // Receives notification if the database if there were any changes
            return cursor;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
//                throw new IllegalArgumentException("Error inserting new product - URI: " + uri + ", ContentValues size: " + values.size() + ", " + values.valueSet());
                Toast.makeText(context, "Error inserting new product\nMatch #: " + match, Toast.LENGTH_SHORT).show();
                return null;
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        double productPrice = values.getAsDouble(BookEntry.COLUMN_PRODUCT_PRICE);
        int productQuantity = values.getAsInteger(BookEntry.COLUMN_PRODUCT_QUANTITY);
        String productAuthor = values.getAsString(BookEntry.COLUMN_PRODUCT_AUTHOR);
        String productISBN13 = values.getAsString(BookEntry.COLUMN_PRODUCT_ISBN_13);
        String productISBN10 = values.getAsString(BookEntry.COLUMN_PRODUCT_ISBN_10);
        String productSupplierName = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        String productSupplierPhone = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        throwNewIllegalArgumentException(productName, R.string.illegal_argument_exception_require_name);
        throwNewIllegalArgumentException(productAuthor, R.string.illegal_argument_exception_require_author);
        throwNewIllegalArgumentException(productPrice, R.string.illegal_argument_exception_require_price);
        throwNewIllegalArgumentException(productISBN13, R.string.illegal_argument_exception_require_isbn);
        throwNewIllegalArgumentException(productISBN10, R.string.illegal_argument_exception_require_isbn);
        if (productQuantity < 0) {
            throw new IllegalArgumentException(context.getResources().getString(R.string.illegal_argument_exception_require_quantity));
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(BookEntry.COLUMN_PRODUCT_AUTHOR, productAuthor);
        contentValues.put(BookEntry.COLUMN_PRODUCT_ISBN_13, productISBN13);
        contentValues.put(BookEntry.COLUMN_PRODUCT_ISBN_10, productISBN10);
        contentValues.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
        contentValues.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhone);
        contentValues.put(BookEntry.COLUMN_PRODUCT_PRICE, productPrice);
        contentValues.put(BookEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        long newRowID = db.insert(BookEntry.TABLE_NAME, null, contentValues);
        if (newRowID != BAD_ID) {
            Toast.makeText(context, context.getResources().getString(R.string.successful_data_saved) + " new row ID: " + newRowID, Toast.LENGTH_SHORT).show();
            context.getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, newRowID);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.unsuccessful_data_saved), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void throwNewIllegalArgumentException(Object value, int thrownStringID) {
        if (value instanceof String) {
            if (TextUtils.isEmpty((String) value)) {
                throw new IllegalArgumentException(context.getResources().getString(thrownStringID));
            }
        } else if (value instanceof Integer) {
            if ((int) value < 0) {
                throw new IllegalArgumentException(context.getResources().getString(thrownStringID));
            }
        } else if (value instanceof Double) {
            if ((double) value < 0) {
                throw new IllegalArgumentException(context.getResources().getString(thrownStringID));
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int affectedRows = BAD_ID;
        switch (match) {
            case PRODUCTS:      // when deleting all products...
                return deleteProduct(uri, selection, selectionArgs);
            case PRODUCTS_ID:   // when deleting selected product...
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Invalid uri for deletion...");
        }
    }

    private int deleteProduct(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int deletedRows = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues userInputValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, userInputValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Invalid uri for update...");
        }
    }

    private int updateProduct(Uri uri, ContentValues userInputValues, String selection, String[] selectionArgs) {
        int newRowID = BAD_ID;
        ContentValues newValues = new ContentValues();
        if (userInputValues.size() == zero) {
            Log.i("TEST", "Values equal 0 in updateProduct()");
            return zero;
        } else {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_NAME)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_NAME, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_AUTHOR)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_AUTHOR, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_ISBN_13)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_ISBN_13, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_ISBN_10)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_ISBN_10, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_PRICE)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_PRICE, userInputValues, newValues);
            }
            if (checkForValidKey(userInputValues, BookEntry.COLUMN_PRODUCT_QUANTITY)) {
                putValueToNewContentValues(BookEntry.COLUMN_PRODUCT_QUANTITY, userInputValues, newValues);
            }
            if (checkContentValues(userInputValues, BookEntry.INPUT_ATTRIBUTE_LIST)) {      // attribute input provided
                newRowID = db.update(BookEntry.TABLE_NAME, newValues, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
            } else {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.error_input_provide_attributes));
            }
        }
        return newRowID;
    }

    private boolean checkForValidKey(ContentValues values, String key) {
        if (values.containsKey(key) && !TextUtils.isEmpty(String.valueOf(values.get(key)))) {
            return true;
        }
        return false;
    }

//    private boolean checkForValidKey(ContentValues values, String key, boolean bool) {
//        int isbn13Length = 13;
//        int isbn10Length = 10;
//        if (values.containsKey(key) && !TextUtils.isEmpty(String.valueOf(values.get(key)))) {
//            String input = String.valueOf(values.get(key));
//            if (key.equals(BookEntry.COLUMN_PRODUCT_ISBN_13)) {
//                if (input.length() == isbn13Length) {
//                    return true;
//                } else {
//                    createToast("Invalid ISBN Number");
//                }
//            }
//            if (key.equals(BookEntry.COLUMN_PRODUCT_ISBN_10)) {
//                if (input.length() == isbn10Length) {
//                    return true;
//                } else {
//                    createToast("Invalid ISBN Number");
//                }
//            }
//        }
//        return false;
//    }

    private void putValueToNewContentValues(String key, ContentValues oldValues, ContentValues newValues) {
        String value = null;
        if (oldValues.get(key) instanceof String) {                    // is a String
            value = oldValues.getAsString(key);
        } else if (oldValues.get(key) instanceof Integer) {             // is an Integer
            value = String.valueOf(oldValues.getAsInteger(key));
        } else if (oldValues.get(key) instanceof Double) {              // is a Double... for some reason
            value = String.valueOf(oldValues.getAsDouble(key));
        }
        newValues.put(key, value);
    }

    private boolean checkContentValues(ContentValues values, String[] list) {
        int listSize = list.length;
        int keyAmount = 0;
        for (int i = zero; i < listSize; i++) {
            if (values.containsKey(list[i])) {
                keyAmount++;
            }
        }
        if (keyAmount <= zero) {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return BookEntry.CONTENT_DIR_TYPE;
            case PRODUCTS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private void createToast(String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

    /*
    *
    TODO: Create Unsaved Data Dialog and Delete Dialog
    *
    */
}
