package com.seebaldtart.projectinventoryapp;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.Tools;
import com.seebaldtart.projectinventoryapp.data.InventoryDBHelper;
import java.text.DecimalFormat;
public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private InventoryDBHelper DBHelper;
    private final int addProduct = R.id.action_add_product;
    private final int editProduct = R.id.action_edit_product;
    private final int removeProduct = R.id.action_remove_product;
    private final int deleteProducts = R.id.action_delete_all_products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUI();
    }
    private void updateUI() {           // Read Database information
        setUpViews();
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
        String newLine = "\n";
        String doubleNewLine = "\n\n";
        String tabNewLine = "\n\t\t";
        String space = " ";
        String separator = " - ";
        String dollarSign = "$";
        DBHelper = new InventoryDBHelper(this);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        Cursor cursor = db.query(BookEntry.TABLE_NAME, null, null, null, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                String currentCountText = getString(R.string.current_inventory_size) + space + cursor.getCount();
                String tableDetailsText = getString(R.string.table_details);
                String columnDetails = getString(R.string.id_string) + space
                        + BookEntry.PRODUCT_ID
                        + separator + BookEntry.PRODUCT_NAME
                        + tabNewLine + BookEntry.PRICE
                        + tabNewLine + BookEntry.QUANTITY
                        + tabNewLine + BookEntry.SUPPLIER_NAME
                        + tabNewLine + BookEntry.SUPPLIER_PHONE_NUMBER;
                builder.append(currentCountText)
                        .append(doubleNewLine)
                        .append(tableDetailsText)
                        .append(newLine)
                        .append(columnDetails);
                int productIDColumnIndex = cursor.getColumnIndex(BookEntry.PRODUCT_ID);
                int productNameColumnIndex = cursor.getColumnIndex(BookEntry.PRODUCT_NAME);
                int productPriceColumnIndex = cursor.getColumnIndex(BookEntry.PRICE);
                int productQuantityColumnIndex = cursor.getColumnIndex(BookEntry.QUANTITY);
                int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_NAME);
                int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_PHONE_NUMBER);
                DecimalFormat formatter = new DecimalFormat(".00");
                while (cursor.moveToNext()) {
                    builder.append(doubleNewLine);
                    String productID = String.valueOf(cursor.getInt(productIDColumnIndex));
                    String productName = cursor.getString(productNameColumnIndex);
                    Double price = cursor.getDouble(productPriceColumnIndex);
                    String productPrice = dollarSign + String.valueOf(formatter.format(price));
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
                    String currentProductDetails = getString(R.string.id_string) + space
                            + productID
                            + separator + productName
                            + tabNewLine + productPrice
                            + tabNewLine + productQuantity
                            + tabNewLine + supplierName
                            + tabNewLine + supplierPhoneNumber;
                    builder.append(currentProductDetails);
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_entry), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
        }
        return builder.toString();
    }
    private void setUpViews() {
        textView = findViewById(R.id.text_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determineEditorActivity(addProduct);
            }
        });
    }
    private void startEditorActivity(String action) {
        if (action != null) {
            Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.editor_bundle), action);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    private void determineEditorActivity(int id) {
        switch (id) {
            case addProduct:
                startEditorActivity(Tools.add);
                break;
            case editProduct:
                startEditorActivity(Tools.edit);
                break;
            case removeProduct:
                startEditorActivity(Tools.remove);
                break;
            case deleteProducts:
                startEditorActivity(Tools.deleteAll);
                break;
        }
    }
    @Override
    protected void onStart() {
        updateUI();
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        determineEditorActivity(item.getItemId());
        return true;
    }
}