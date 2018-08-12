package com.seebaldtart.projectinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.seebaldtart.projectinventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_PRODUCTS_ID = PATH_PRODUCTS + "/#";

    public static class BookEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_ISBN_13 = "product_isbn_13";
        public static final String COLUMN_PRODUCT_ISBN_10 = "product_isbn_10";
        public static final String COLUMN_PRODUCT_AUTHOR = "product_author";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "product_supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "product_supplier_phone_number";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final int MAX_ISBN_13 = 13;
        public static final int MAX_ISBN_10 = 10;

        public static final String[] INPUT_ATTRIBUTE_LIST = {
                COLUMN_PRODUCT_NAME,
                COLUMN_PRODUCT_AUTHOR,
                COLUMN_PRODUCT_ISBN_13,
                COLUMN_PRODUCT_ISBN_10,
                COLUMN_PRODUCT_SUPPLIER_NAME,
                COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
                COLUMN_PRODUCT_PRICE,
                COLUMN_PRODUCT_QUANTITY
        };

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY;
    }
}