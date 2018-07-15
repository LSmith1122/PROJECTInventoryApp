package com.seebaldtart.projectinventoryapp;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
import com.seebaldtart.projectinventoryapp.data.InventoryDBHelper;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private InventoryDBHelper DBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUI();
    }
    private void updateUI() {           // Read Database information
        setUpTextView();
        String displayText = "";
        String dataInfo = queryData();
        if (TextUtils.isEmpty(dataInfo)) {
            displayText = getString(R.string.display_text_empty);
        } else {
            displayText = dataInfo;
        }
        textView.setText(displayText);
    }
    private String queryData() {
        DBHelper = new InventoryDBHelper(this);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        Cursor cursor = db.query(BookEntry.TABLE_NAME, null, null, null, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                String currentCountText = getString(R.string.current_inventory_size) + " " + cursor.getCount();
                String tableDetailsText = getString(R.string.table_details);
                String columnDetails = getString(R.string.id_string) + " "
                        + BookEntry.PRODUCT_ID
                        + " - " + BookEntry.PRODUCT_NAME
                        + "\n\t" + BookEntry.PRICE
                        + "\n\t" + BookEntry.QUANTITY
                        + "\n\t" + BookEntry.SUPPLIER_NAME
                        + "\n\t" + BookEntry.SUPPLIER_PHONE_NUMBER;
                builder.append(currentCountText)
                        .append("\n\n")
                        .append(tableDetailsText)
                        .append("\n")
                        .append(columnDetails);
                int productIDColumnIndex = cursor.getColumnIndex(BookEntry.PRODUCT_ID);
                int productNameColumnIndex = cursor.getColumnIndex(BookEntry.PRODUCT_NAME);
                int productPriceColumnIndex = cursor.getColumnIndex(BookEntry.PRICE);
                int productQuantityColumnIndex = cursor.getColumnIndex(BookEntry.QUANTITY);
                int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_NAME);
                int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_PHONE_NUMBER);
                DecimalFormat formatter = new DecimalFormat(".00");
                while (cursor.moveToNext()) {
                    builder.append("\n\n");
                    String productID = String.valueOf(cursor.getInt(productIDColumnIndex));
                    String productName = cursor.getString(productNameColumnIndex);
                    Double price = Double.valueOf(cursor.getFloat(productPriceColumnIndex));
                    String productPrice = "$" + String.valueOf(formatter.format(price));
                    String productQuantity = String.valueOf(cursor.getInt(productQuantityColumnIndex));
                    String supplierName = "";
                    String supplierPhoneNumber = "";
                    if (!cursor.getString(supplierNameColumnIndex).isEmpty() && cursor.getString(supplierNameColumnIndex) != null) {
                        supplierName = cursor.getString(supplierNameColumnIndex);
                    } else {
                        supplierName = getString(R.string.unknown_entry);
                    }
                    if (!cursor.getString(supplierPhoneNumberColumnIndex).isEmpty() && cursor.getString(supplierPhoneNumberColumnIndex) != null) {
                        supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
                    } else {
                        supplierPhoneNumber = getString(R.string.unknown_entry);
                    }
                    String currentProductDetails = getString(R.string.id_string) + " "
                            + productID
                            + " - " + productName
                            + "\n\t" + productPrice
                            + "\n\t" + productQuantity
                            + "\n\t" + supplierName
                            + "\n\t" + supplierPhoneNumber;
                    builder.append(currentProductDetails);
                }
            }
        } finally {
            cursor.close();
        }
        return builder.toString();
    }
    private void setUpTextView() {
        textView = findViewById(R.id.text_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        updateUI();
        super.onStart();
    }
}