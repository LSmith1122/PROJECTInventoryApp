package com.seebaldtart.projectinventoryapp;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.Tools;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
import com.seebaldtart.projectinventoryapp.data.InventoryDBHelper;
public class EditorActivity extends AppCompatActivity {
    private final long invalid = -1;
    private TextView descriptionText;
    private EditText productID;
    private LinearLayout productIDGroup;
    private LinearLayout contentGroup;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNumberEditText;
    private FloatingActionButton saveButton;
    private FloatingActionButton cancelButton;
    private InventoryDBHelper DBHelper;
//    private final String add = "action_add";
//    private final String edit = "action_edit";
//    private final String remove = "action_remove";
//    private final String deleteAll = "action_delete_all";
    private String layoutStatus = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        DBHelper = new InventoryDBHelper(this);
        Bundle bundle = getIntent().getExtras();
        determineBundle(bundle);
    }
    private void determineBundle(Bundle bundle) {
        try {
            String key = getString(R.string.editor_bundle);
            String value = bundle.getString(key);
            if (value.equals(Tools.add)) {        // Add
                setUpViews(Tools.add);
            } else if (value.equals(Tools.edit)) {        // Edit
                setUpViews(Tools.edit);
            } else if (value.equals(Tools.remove)) {      // Remove
                setUpViews(Tools.remove);
            } else {        // Delete All
                setUpViews(Tools.deleteAll);
            }
        } catch (NullPointerException e) {
            Log.i("ERROR", "There is a problem getting bundles...");
        }
    }
    private void setUpViews(String value) {
        descriptionText = findViewById(R.id.description_text_view);
        productIDGroup = findViewById(R.id.id_group);
        productID = findViewById(R.id.product_id);
        contentGroup = findViewById(R.id.content_group);
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
                if (updateProduct() != invalid) {      // Valid entry
                    switch (layoutStatus) {
                        case Tools.add:
                            Toast.makeText(EditorActivity.this, "New Product Added: " + productNameEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                            break;
                        case Tools.edit:
                            Toast.makeText(EditorActivity.this, "Product Updated: " + productNameEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                            break;
                        case Tools.remove:
                            Toast.makeText(EditorActivity.this, "Product Removed", Toast.LENGTH_SHORT).show();
                            break;
                        case Tools.deleteAll:
                            Toast.makeText(EditorActivity.this, "All Products Deleted from: " + BookEntry.TABLE_NAME, Toast.LENGTH_LONG).show();
                            break;
                    }
                    finish();
                } else {
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
        determineLayout(value);
    }
    private void determineLayout(String value) {
        switch (value) {
        case Tools.add:
            layoutStatus = Tools.add;     // To determine behavior of FAB
            descriptionText.setText(getString(R.string.editor_label_add_description));
            setLayoutVisibilities(View.GONE, View.VISIBLE, R.drawable.baseline_check_white_48);
            break;
        case Tools.edit:
            layoutStatus = Tools.edit;
            descriptionText.setText(getString(R.string.editor_label_edit_description));
            setLayoutVisibilities(View.VISIBLE, View.VISIBLE, R.drawable.baseline_edit_white_48);
            break;
        case Tools.remove:
            layoutStatus = Tools.remove;
            descriptionText.setText(getString(R.string.editor_label_remove_description));
            setLayoutVisibilities(View.VISIBLE, View.GONE, R.drawable.baseline_delete_white_48);
            break;
        case Tools.deleteAll:
            layoutStatus = Tools.deleteAll;
            descriptionText.setText(getString(R.string.editor_label_delete_description));
            setLayoutVisibilities(View.GONE, View.GONE, R.drawable.baseline_delete_white_48);
            break;
        default:
            layoutStatus = Tools.add;
            descriptionText.setText(getString(R.string.editor_label_add_description));
            setLayoutVisibilities(View.GONE, View.VISIBLE, R.drawable.baseline_check_white_48);
            break;
        }
    }
    private void setLayoutVisibilities(int value1, int value2, int imgID) {
        productIDGroup.setVisibility(value1);
        contentGroup.setVisibility(value2);
        saveButton.setImageResource(imgID);
    }
    private long updateProduct() {
        long newRowID = invalid;
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        if (!layoutStatus.equals(Tools.deleteAll) && !layoutStatus.equals(Tools.remove)) {
            try {
                ContentValues contentValues = new ContentValues();
                String productName = productNameEditText.getText().toString().trim();
                Double productPrice = Double.valueOf(productPriceEditText.getText().toString().trim());
                int productQuantity = Integer.valueOf(productQuantityEditText.getText().toString().trim());
                String supplierName = checkValues(supplierNameEditText.getText().toString().trim());
                String supplierPhoneNumber = checkValues(supplierPhoneNumberEditText.getText().toString().trim());
                contentValues.put(BookEntry.PRODUCT_NAME, productName);
                contentValues.put(BookEntry.PRICE, productPrice);
                contentValues.put(BookEntry.QUANTITY, productQuantity);
                contentValues.put(BookEntry.SUPPLIER_NAME, supplierName);
                contentValues.put(BookEntry.SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);
                switch (layoutStatus) {
                    case Tools.add:
                        newRowID = db.insert(BookEntry.TABLE_NAME, null, contentValues);
                        break;
                    case Tools.edit:
                        if (!TextUtils.isEmpty(productID.getText().toString())) {
                            int ID = Integer.valueOf(productID.getText().toString());
                            String whereClause = BookEntry.PRODUCT_ID + " == " + ID;
                            newRowID = db.update(BookEntry.TABLE_NAME, contentValues, whereClause, null);
                        }
                        break;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Entry", Toast.LENGTH_SHORT).show();
            }
        } else {
            switch (layoutStatus) {
                case Tools.remove:
                    if (!TextUtils.isEmpty(productID.getText().toString())) {
                        int ID = Integer.valueOf(productID.getText().toString());
                        String whereClause = BookEntry.PRODUCT_ID + " == " + ID;
                        newRowID = db.delete(BookEntry.TABLE_NAME, whereClause, null);
                    }
                    break;
                case Tools.deleteAll:
                    newRowID = db.delete(BookEntry.TABLE_NAME, null, null);
                    deleteDatabase(DBHelper.DATABASE_NAME);
                    break;
            }
        }
        return newRowID;
    }
    private String checkValues (String values) {
        String newString = "";
        if (values.isEmpty()) {
            newString = getString(R.string.unknown_entry);
        } else {
            newString = values;
        }
        return newString;
    }
}