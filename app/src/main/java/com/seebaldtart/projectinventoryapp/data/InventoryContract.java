package com.seebaldtart.projectinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.seebaldtart.projectinventoryapp.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    public static class BookEntry implements BaseColumns{
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_PRODUCT_ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_PRODUCT_ISBN_10 = "product_isbn_10";
        public static final String COLUMN_PRODUCT_ISBN_13 = "product_isbn_13";
        public static final String COLUMN_PRODUCT_AUTHOR = "product_author";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "product_supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "product_supplier_phone_number";

        public static final Uri SELECTED_URI_NULL = null;

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
    }
    public static class Tools {
        public final static String add = "action_add";
        public final static String edit = "action_edit";
        public final static String remove = "action_remove";
        public final static String deleteAll = "action_delete_all";
    }
}