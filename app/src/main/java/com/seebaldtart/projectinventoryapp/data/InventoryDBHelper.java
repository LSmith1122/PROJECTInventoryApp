package com.seebaldtart.projectinventoryapp.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
public class InventoryDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;
    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry.PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookEntry.PRODUCT_NAME + " TEXT NOT NULL,"
                + BookEntry.PRICE + " DECIMAL NOT NULL,"
                + BookEntry.QUANTITY + " INTEGER NOT NULL,"
                + BookEntry.SUPPLIER_NAME + " TEXT,"
                + BookEntry.SUPPLIER_PHONE_NUMBER + " TEXT"
                + ");";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
