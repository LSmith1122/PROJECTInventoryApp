package com.seebaldtart.projectinventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.seebaldtart.projectinventoryapp.R;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

public class ProductCursorAdapter extends CursorAdapter {
    private TextView nameText;
    private TextView priceText;
    private TextView isbnText;
    private TextView quantityText;
    private final String emptySpace = " ";

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View parent, Context context, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            try {
                initViews(parent);
                int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
                int isbn13ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_13);
                int isbn10ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_10);
                int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
                String currentName = String.valueOf(cursor.getString(nameColumnIndex));
                String currentPrice = String.valueOf(cursor.getInt(priceColumnIndex));
                String currentISBN13 = String.valueOf(cursor.getString(isbn13ColumnIndex));
                String currentISBN10 = String.valueOf(cursor.getString(isbn10ColumnIndex));
                String currentQuantity = String.valueOf(cursor.getString(quantityColumnIndex));
                nameText.setText(currentName);
                priceText.setText(currentPrice);
                setTextForISBN(context, currentISBN13, currentISBN10);
                setTextForQuantity(context, currentQuantity);
            } catch (IllegalArgumentException e) {
                Log.e("Error", "Error binding view with Cursor data", e);
            }
        }
    }

    private void initViews(View parent) {
        nameText = (TextView) parent.findViewById(R.id.product_name_edit_text);
        priceText = (TextView) parent.findViewById(R.id.product_price_edit_text);
        isbnText = (TextView) parent.findViewById(R.id.product_isbn_edit_text);
        quantityText = (TextView) parent.findViewById(R.id.product_quantity_edit_text);
    }

    private void setTextForISBN(Context context, String isbn13, String isbn10) {
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
        isbnText.setText(builder.toString());
    }

    private void setTextForQuantity(Context context, String quantityString) {
        final String BASE_QUANTITY_IN_STOCK = context.getResources().getString(R.string.quantity_in_stock);
        String quantity = quantityString + emptySpace + BASE_QUANTITY_IN_STOCK;
        quantityText.setText(quantity);
    }
}
