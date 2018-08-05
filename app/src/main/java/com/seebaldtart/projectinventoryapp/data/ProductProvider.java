package com.seebaldtart.projectinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.UriMatcher;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.R;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

public class ProductProvider extends ContentProvider {
    private final String LOG_TAG = ProductProvider.class.getSimpleName();
    public static final int PRODUCTS = 100;
    public static final int PRODUCTS_ID = 101;
    private final int BAD_ID = -1;
    private final int zero = 0;
    private ProductDBHelper mDBHelper;
    public static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
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
            switch (sUriMatcher.match(uri)) {
                case PRODUCTS:
                    cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                case PRODUCTS_ID:
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
                throw new IllegalArgumentException();
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        double productPrice = values.getAsDouble(BookEntry.COLUMN_PRODUCT_PRICE);
        int productQuantity = values.getAsInteger(BookEntry.COLUMN_PRODUCT_QUANTITY);
        String productAuthor = values.getAsString(BookEntry.COLUMN_PRODUCT_AUTHOR);
        int productISBN13 = values.getAsInteger(BookEntry.COLUMN_PRODUCT_ISBN_13);
        int productISBN10 = values.getAsInteger(BookEntry.COLUMN_PRODUCT_ISBN_10);
        String productSupplierName = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        String productSupplierPhone = values.getAsString(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        throwNewIllegalArgumentExceptionForString(productName, R.string.illegal_argument_exception_require_name);
        throwNewIllegalArgumentExceptionForString(productAuthor, R.string.illegal_argument_exception_require_author);
        throwNewIllegalArgumentExceptionForInteger((int) productPrice, R.string.illegal_argument_exception_require_price);
        throwNewIllegalArgumentExceptionForInteger(productISBN13, R.string.illegal_argument_exception_require_isbn);
        throwNewIllegalArgumentExceptionForInteger(productISBN10, R.string.illegal_argument_exception_require_isbn);
        if (productQuantity < 0) { throw new IllegalArgumentException(context.getResources().getString(R.string.illegal_argument_exception_require_quantity)); }
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(BookEntry.COLUMN_PRODUCT_PRICE, productPrice);
        contentValues.put(BookEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(BookEntry.COLUMN_PRODUCT_AUTHOR, productAuthor);
        contentValues.put(BookEntry.COLUMN_PRODUCT_ISBN_13, productISBN13);
        contentValues.put(BookEntry.COLUMN_PRODUCT_ISBN_10, productISBN10);
        contentValues.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
        contentValues.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhone);
        long newRowID = db.insert(BookEntry.TABLE_NAME, null, contentValues);
        if (newRowID != BAD_ID) {
            Toast.makeText(context, context.getResources().getString(R.string.successful_data_saved), Toast.LENGTH_SHORT).show();
            context.getContentResolver().notifyChange(uri, null);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.unsuccessful_data_saved), Toast.LENGTH_SHORT).show();
        }
        return ContentUris.withAppendedId(uri, newRowID);
    }

    private void throwNewIllegalArgumentExceptionForString(String value, int thrownStringID) {
        if (value == null || TextUtils.isEmpty(value)) {
            throw new IllegalArgumentException(context.getResources().getString(thrownStringID));
        }
    }

    private void throwNewIllegalArgumentExceptionForInteger(int value, int thrownStringID) {
        if (value <= 0) {
            throw new IllegalArgumentException(context.getResources().getString(thrownStringID));
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // TODO: Get the writable database and return the affected (deleted) Row ID (Integer) if Row ID > 0
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // TODO: Get the writable database and return the affected (updated) Row ID (Integer)
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        return 0;
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
}
