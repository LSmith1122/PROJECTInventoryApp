package com.seebaldtart.projectinventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.EditorActivity;
import com.seebaldtart.projectinventoryapp.R;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

import java.text.DecimalFormat;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }
    private Context mContext;
    private Uri mSelectedURI;
    private int productID;
    private long id;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("Error", "context: " + context.toString() + " cursor count:" + cursor.getCount() + " parent: " + parent);
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        mContext = context;
        return convertView;
    }

    @Override
    public void bindView(View parent, final Context context, final Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            productID = cursor.getColumnIndexOrThrow(BookEntry._ID);
            id = cursor.getLong(productID);
            mSelectedURI = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
            RelativeLayout mainGroup = (RelativeLayout) parent.findViewById(R.id.main_content_group);
            TextView nameText = (TextView) parent.findViewById(R.id.product_name);
            TextView priceText = (TextView) parent.findViewById(R.id.product_price);
            TextView isbnText = (TextView) parent.findViewById(R.id.product_isbn);
            TextView quantityText = (TextView) parent.findViewById(R.id.product_quantity);
            Button orderButton = (Button) parent.findViewById(R.id.order_button);
            int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
            int isbn13ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_13);
            int isbn10ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_10);
            int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
            String currentName = String.valueOf(cursor.getString(nameColumnIndex));
            double currentPrice = cursor.getDouble(priceColumnIndex);
            String currentISBN13 = String.valueOf(cursor.getString(isbn13ColumnIndex));
            final String currentISBN10 = String.valueOf(cursor.getString(isbn10ColumnIndex));
            final String currentQuantity = String.valueOf(cursor.getString(quantityColumnIndex));
            nameText.setText(currentName);
            priceText.setText(createTextForPrice(currentPrice));
            isbnText.setText(createTextForISBN(mContext, currentISBN13, currentISBN10));
            quantityText.setText(createTextForQuantity(mContext, currentQuantity));
            mainGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cursor != null) {
                        startEditorActivity(mContext, mSelectedURI);
                    }
                }
            });
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cursor != null) {
                        int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
                        int currentQuantity = cursor.getInt(quantityColumnIndex);
                        if (currentQuantity > 0) {
                            updateQuantity(context, cursor, currentQuantity);           // Decrease quantity in database
                            int supplierPhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
                            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplierPhone));
                            context.startActivity(intent);
                        } else {                        // Quantity is too low, notify User
                            createToast(mContext.getResources().getString(R.string.no_products_in_inventory));
                        }
                    }
                }
            });
        }
    }

    private void updateQuantity(Context context, Cursor cursor, int currentQuantity) {
        int zero= 0;
        int quantity = currentQuantity;
        int newQuantity = quantity - 1;
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
        int updatedRowID = context.getContentResolver().update(mSelectedURI, values, null, null);
        if (updatedRowID > 0) {
            createToast(mContext.getResources().getString(R.string.successful_product_sale));
        } else {
            createToast(mContext.getResources().getString(R.string.unsuccessful_product_sale_update));
        }
    }

    private void createToast(String messageString) {
        Toast.makeText(mContext, messageString, Toast.LENGTH_SHORT).show();
    }

    private void startEditorActivity(Context context, Uri selectedURI) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.setData(selectedURI);
        context.startActivity(intent);
    }

    private String createTextForPrice(double value) {
        DecimalFormat format = new DecimalFormat("0.00");
        return "$" + format.format(value);
    }

    private String createTextForISBN(Context context, String isbn13, String isbn10) {
        final String emptySpace = " ";
        final String BASE_ISBN_13 = context.getResources().getString(R.string.isbn_13);
        final String BASE_ISBN_10 = context.getResources().getString(R.string.isbn_10);
        final String newLine = "\n";
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_ISBN_13)
                .append(emptySpace)
                .append(isbn13)
                .append(newLine)
                .append(BASE_ISBN_10)
                .append(emptySpace)
                .append(isbn10);
        return builder.toString();
    }

    private String createTextForQuantity(Context context, String quantityString) {
        final String emptySpace = " ";
        final String BASE_QUANTITY_IN_STOCK = context.getResources().getString(R.string.quantity_in_stock);
        String quantity = quantityString + emptySpace + BASE_QUANTITY_IN_STOCK;
        return quantity;
    }
}
