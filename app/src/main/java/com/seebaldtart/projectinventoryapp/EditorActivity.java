package com.seebaldtart.projectinventoryapp;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
import com.seebaldtart.projectinventoryapp.data.InventoryDBHelper;
public class EditorActivity extends AppCompatActivity {
    private final long invalid = -1;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNumberEditText;
    private FloatingActionButton saveButton;
    private FloatingActionButton cancelButton;
    private InventoryDBHelper DBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        productNameEditText = findViewById(R.id.product_name_edit_text);
        productPriceEditText = findViewById(R.id.product_price_edit_text);
        productQuantityEditText = findViewById(R.id.product_quantity_edit_text);
        supplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        supplierPhoneNumberEditText = findViewById(R.id.supplier_phone_number_edit_text);
        saveButton = findViewById(R.id.save_product_button);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateDatabase() != invalid) {      // Valid entry
                    Toast.makeText(EditorActivity.this, "New Product Added: " + productNameEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {        // Invalid entry
                    Toast.makeText(EditorActivity.this, "Invalid Entry", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DBHelper = new InventoryDBHelper(this);
    }
    private long updateDatabase() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String productName = productNameEditText.getText().toString().trim();
        Double productPrice = Double.valueOf(productPriceEditText.getText().toString().trim());
        int productQuantity = Integer.valueOf(productQuantityEditText.getText().toString().trim());
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = supplierPhoneNumberEditText.getText().toString().trim();
        contentValues.put(BookEntry.PRODUCT_NAME, productName);
        contentValues.put(BookEntry.PRICE, productPrice);
        contentValues.put(BookEntry.QUANTITY, productQuantity);
        contentValues.put(BookEntry.SUPPLIER_NAME, supplierName);
        contentValues.put(BookEntry.SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);
        long newRowID = db.insert(BookEntry.TABLE_NAME, null, contentValues);
        return newRowID;
    }
}