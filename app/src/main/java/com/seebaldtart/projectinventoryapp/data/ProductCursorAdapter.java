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
import android.widget.TextView;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.R;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

import java.text.DecimalFormat;

public class ProductCursorAdapter extends CursorAdapter {
    final int zero = 0;

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    private Context mContext;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return convertView;
    }

    @Override
    public void bindView(View parent, final Context context, final Cursor cursor) {
        mContext = context;
        if (cursor != null && cursor.getCount() > 0) {
            Button saleButton = (Button) parent.findViewById(R.id.sale_button);
            TextView nameText = (TextView) parent.findViewById(R.id.product_name);
            TextView priceText = (TextView) parent.findViewById(R.id.product_price);
            TextView isbnText = (TextView) parent.findViewById(R.id.product_isbn);
            TextView quantityText = (TextView) parent.findViewById(R.id.product_quantity);
            int idColumnIndex = cursor.getColumnIndexOrThrow(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
            int isbn13ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_13);
            int isbn10ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_10);
            int supplierPhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
            final long currentID = cursor.getLong(idColumnIndex);
            String currentName = String.valueOf(cursor.getString(nameColumnIndex));
            double currentPrice = cursor.getDouble(priceColumnIndex);
            String currentISBN13 = String.valueOf(cursor.getString(isbn13ColumnIndex));
            final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            final String currentISBN10 = String.valueOf(cursor.getString(isbn10ColumnIndex));
            final int currentQuantity = cursor.getInt(quantityColumnIndex);
            nameText.setText(currentName);
            priceText.setText(createTextForPrice(currentPrice));
            isbnText.setText(createTextForISBN(mContext, currentISBN13, currentISBN10));
            quantityText.setText(createTextForQuantity(mContext, String.valueOf(currentQuantity)));
            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final long id = currentID;
                    final int quantity = currentQuantity;
                    final String phone = supplierPhone;
                    Log.i("TEST", "Current ID: " + id + " and Current Quantity: " + quantity);
                    initSale(id, quantity, phone);
                }
            });
        }
    }

    private void initSale(long id, int currentQuantity, String supplierPhone) {
        if (currentQuantity > 0) {
            updateQuantity(mContext, currentQuantity, id);           // Decrease quantity in database
        } else {                        // Quantity is too low, notify User
            createToast(mContext.getResources().getString(R.string.no_products_in_inventory));
        }
    }

    private void updateQuantity(Context context, int currentQuantity, long id) {
        int newQuantity = currentQuantity - 1;
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
        Uri mSelectedURI = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
        int updatedRowID = mContext.getContentResolver().update(mSelectedURI, values, null, null);
        if (updatedRowID > zero) {
            createToast(mContext.getResources().getString(R.string.successful_product_sale));
        } else {
            createToast(mContext.getResources().getString(R.string.unsuccessful_product_sale_update));
        }
    }

    private void createToast(String messageString) {
        Toast.makeText(mContext, messageString, Toast.LENGTH_SHORT).show();
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
