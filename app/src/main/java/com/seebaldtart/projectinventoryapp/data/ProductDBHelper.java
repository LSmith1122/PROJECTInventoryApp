package com.seebaldtart.projectinventoryapp.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
public class ProductDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "book.db";
    public static final int DATABASE_VERSION = 1;
    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + BookEntry.COLUMN_PRODUCT_PRICE + " DECIMAL NOT NULL,"
                + BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL,"
                + BookEntry.COLUMN_PRODUCT_ISBN_13 + " TEXT,"
                + BookEntry.COLUMN_PRODUCT_ISBN_10 + " TEXT,"
                + BookEntry.COLUMN_PRODUCT_AUTHOR + " TEXT NOT NULL,"
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT,"
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT"
                + ");";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}