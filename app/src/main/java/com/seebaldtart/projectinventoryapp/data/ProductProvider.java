package com.seebaldtart.projectinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

public class ProductProvider extends ContentProvider {
    public static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int PRODUCTS = 100;
    public static final int PRODUCTS_ID = 101;
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // TODO: Get the readable database and return a cursor (query)
        return null;
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // TODO: Get the writable database and return the newly added Uri
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // TODO: Get the writable database and return the affected (deleted) Row ID (Integer) if Row ID > 0
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // TODO: Get the writable database and return the affected (updated) Row ID (Integer)
        return 0;
    }
}
