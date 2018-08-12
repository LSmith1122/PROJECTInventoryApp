package com.seebaldtart.projectinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int INVALID = -1;
    private final int zero = 0;
    private final int CURSOR_LOADER_ID = 1;
    private String LOG_TAG;
    private TextView descriptionText;
    private LinearLayout contentGroup;
    private EditText productNameEditText;
    private EditText productAuthorEditText;
    private EditText productISBN13EditText;
    private EditText productISBN10EditText;
    private EditText productSupplierNameEditText;
    private EditText productSupplierPhoneNumberEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private ImageButton productQuantityButtonIncrement;
    private ImageButton productQuantityButtonDecrement;
    private FloatingActionButton saveButton;
    private FloatingActionButton cancelButton;
    private boolean mHasBeenTouched = false;
    private Uri mSelectedURI = null;
    private Context context;
    private String productName;
    private String productAuthor;
    private String productISBN13;
    private String productISBN10;
    private String productSupplierName;
    private String productSupplierPhone;
    private double productPrice;
    private int productQuantity;
    private int mCurrentQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        context = getApplicationContext();
        LOG_TAG = context.toString();
        retrieveIntentData();
        setUpViews();
        if (mSelectedURI != null) {
            descriptionText.setText(context.getResources().getString(R.string.editor_update_description));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(CURSOR_LOADER_ID, null, this);
        } else {
            descriptionText.setText(context.getResources().getString(R.string.editor_add_description));
            productQuantityEditText.setText(String.valueOf(zero));
            initButtonOnClickListeners();
        }
    }

    private void initButtonOnClickListeners() {
        productQuantityButtonIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuantity = getQuantityValue();
                mCurrentQuantity++;
                productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
            }
        });
        productQuantityButtonDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuantity = getQuantityValue();
                mCurrentQuantity--;
                if (mCurrentQuantity < 0) {
                    mCurrentQuantity = 0;
                }
                productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
            }
        });
    }

    private int getQuantityValue() {
        return Integer.parseInt(productQuantityEditText.getText().toString());
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        mSelectedURI = intent.getData();
        if (mSelectedURI != null) {
            this.setTitle(R.string.editor_label_update);
        } else {
            this.setTitle(R.string.editor_label_add);
        }
    }

    private void updateUI(Cursor cursor) {
        if (cursor.getCount() > zero) {
            try {
                cursor.moveToNext();
                int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
                int authorColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_AUTHOR);
                int isbn13ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_13);
                int isbn10ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_10);
                int supplierNameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
                int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
                String name = cursor.getString(nameColumnIndex);
                String author = cursor.getString(authorColumnIndex);
                String isbn13 = cursor.getString(isbn13ColumnIndex);
                String isbn10 = cursor.getString(isbn10ColumnIndex);
                String supplierName = cursor.getString(supplierNameColumnIndex);
                String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
                double price = cursor.getDouble(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                mCurrentQuantity = quantity;
                productNameEditText.setText(name);
                productAuthorEditText.setText(author);
                productISBN13EditText.setText(compileISBNNumber(isbn13, BookEntry.MAX_ISBN_13));
                productISBN10EditText.setText(compileISBNNumber(isbn10, BookEntry.MAX_ISBN_10));
                productSupplierNameEditText.setText(supplierName);
                compileSupplierPhoneNumber(supplierPhone);
                productPriceEditText.setText(createTextForPrice(price));
                productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Error updating UI", e);
            }
        }
    }

    private String createTextForPrice(double value) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value);
    }

    private boolean isEmpty(String value) {
        return TextUtils.isEmpty(value);
    }

    private void setUpViews() {
        descriptionText = findViewById(R.id.description_text_view);
        contentGroup = findViewById(R.id.content_group);
        productNameEditText = findViewById(R.id.product_name_edit_text);
        productAuthorEditText = findViewById(R.id.product_author_edit_text);
        productISBN13EditText = findViewById(R.id.product_isbn13_edit_text);
        productISBN10EditText = findViewById(R.id.product_isbn10_edit_text);
        productQuantityEditText = findViewById(R.id.product_quantity_edit_text);
        productSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        productSupplierPhoneNumberEditText = findViewById(R.id.supplier_phone_number_edit_text);
        productPriceEditText = findViewById(R.id.product_price_edit_text);
        productQuantityButtonIncrement = findViewById(R.id.product_quantity_increment_button);
        productQuantityButtonDecrement = findViewById(R.id.product_quantity_decrement_button);
        saveButton = findViewById(R.id.save_product_button);
        cancelButton = findViewById(R.id.cancel_button);;
        setupViewOnClickListeners();
    }

    private void setupViewOnClickListeners() {
        initViewOnClickListeners(productNameEditText);
        initViewOnClickListeners(productAuthorEditText);
        initViewOnClickListeners(productISBN13EditText);
        initViewOnClickListeners(productISBN10EditText);
        initViewOnClickListeners(productQuantityEditText);
        initViewOnClickListeners(productSupplierNameEditText);
        initViewOnClickListeners(productSupplierPhoneNumberEditText);
        initViewOnClickListeners(productPriceEditText);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInput();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener discardProgressDialogOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelProgress();
                    }
                };
                showUnsavedChangesDialog(discardProgressDialogOnClickListener);
            }
        });
        productSupplierPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            int mSwitch = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productSupplierPhoneNumberEditText.length();
                if (mSwitch == 1) {
                    if (length > 0) {
                        productSupplierPhoneNumberEditText.removeTextChangedListener(this);
                        compileSupplierPhoneNumber(productSupplierPhoneNumberEditText.getText().toString().trim());
                        productSupplierPhoneNumberEditText.addTextChangedListener(this);
                    }
                } else {
                    mSwitch++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productISBN13EditText.addTextChangedListener(new TextWatcher() {
            int mSwitch = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productISBN13EditText.length();
                if (mSwitch == 1) {
                    if (length > 0) {
                        productISBN13EditText.removeTextChangedListener(this);
                        productISBN13EditText.setText(compileISBNNumber(productISBN13EditText.getText().toString().trim(), BookEntry.MAX_ISBN_13));
                        productISBN13EditText.setSelection(productISBN13EditText.getText().length());
                        productISBN13EditText.addTextChangedListener(this);
                    }
                } else {
                    mSwitch++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productISBN10EditText.addTextChangedListener(new TextWatcher() {
            int mSwitch = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productISBN10EditText.length();
                if (mSwitch == 1) {
                    if (length > 0) {
                        productISBN10EditText.removeTextChangedListener(this);
                        productISBN10EditText.setText(compileISBNNumber(productISBN10EditText.getText().toString().trim(), BookEntry.MAX_ISBN_10));
                        productISBN10EditText.setSelection(productISBN10EditText.getText().length());
                        productISBN10EditText.addTextChangedListener(this);
                    }
                } else {
                    mSwitch++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void compileSupplierPhoneNumber(String value) {
        String appendage = "-";
        String currentString = value;
        extractNumberFromFormat(currentString);
        String beginning, phoneNumberString = "";
        if (!TextUtils.isEmpty(currentString)) {
            phoneNumberString = currentString;
            if (currentString.length() > 3) {
                beginning = "(" + currentString.substring(0,3) + ") ";
                phoneNumberString = beginning;
                if (currentString.length() >= 6) {
                    if (currentString.length() > 6) {
                        phoneNumberString = beginning + currentString.substring(3,6) + appendage + currentString.substring(6);
                    } else {
                        phoneNumberString = beginning + currentString.substring(3);
                    }
                } else {
                    phoneNumberString = beginning + currentString.substring(3);
                }
            }
        }
        productSupplierPhoneNumberEditText.setText(phoneNumberString);
        int lastPos = productSupplierPhoneNumberEditText.length();
        productSupplierPhoneNumberEditText.setSelection(lastPos);
    }

    private String compileISBNNumber(String value, int max) {
        String appendage = "-";
        String ISBNString = "";
        String first, second, third, fourth = "";
        String currentString = extractNumberFromFormat(value);
        switch (max) {
            case BookEntry.MAX_ISBN_13:
                // Example ISBN 13#: 978-3-16-148410-0
                if (!TextUtils.isEmpty(currentString)) {
                    ISBNString = currentString;
                    if (currentString.length() > 3) {
                        first = currentString.substring(0, 3) + appendage;
                        ISBNString = first  + currentString.substring(3);
                        if (currentString.length() > 4) {
                            second = first + currentString.substring(3, 4) + appendage;
                            ISBNString = second + currentString.substring(4);
                            if (currentString.length() > 6) {
                                third = second + currentString.substring(4, 6) + appendage;
                                ISBNString = third + currentString.substring(6);
                                if (currentString.length() > 12) {
                                    fourth = third + currentString.substring(6, 12) + appendage;
                                    ISBNString = fourth + currentString.substring(12);
                                }
                            }
                        }
                    }
                }
                break;
            case BookEntry.MAX_ISBN_10:
                // Example ISBN 10#: 0-19-852663-6
                if (!TextUtils.isEmpty(currentString)) {
                    ISBNString = currentString;
                    if (currentString.length() > 1) {
                        first = currentString.substring(0, 1) + appendage;
                        ISBNString = first  + currentString.substring(1);
                        if (currentString.length() > 3) {
                            second = first + currentString.substring(1, 3) + appendage;
                            ISBNString = second + currentString.substring(3);
                            if (currentString.length() > 9) {
                                third = second + currentString.substring(3, 9) + appendage;
                                ISBNString = third + currentString.substring(9);
                            }
                        }
                    }
                }
                return ISBNString;
            default:
                break;
        }
        Log.i(LOG_TAG, "Initial compileISBN: " + value + " compileISBN: " + ISBNString);
        return ISBNString;
    }

    private void initViewOnClickListeners(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHasBeenTouched = true;
            }
        });
    }

    private void cancelProgress() {
        createToast(context.getResources().getString(R.string.no_data_saved));
        finish();
    }

    private void saveUserInput() {
        ContentValues userInputValues = new ContentValues();
        recordUserInput();
        initImplementContentValues(userInputValues);
        if (mSelectedURI != null) {             // User has selected a specific product to update...
            updateProduct(mSelectedURI, userInputValues);
        } else {                                // User is adding a new product...
            insertProduct(BookEntry.CONTENT_URI, userInputValues);
        }
    }

    private void recordUserInput() {
        productName = productNameEditText.getText().toString().trim();
        productAuthor = productAuthorEditText.getText().toString().trim();
        productSupplierName = productSupplierNameEditText.getText().toString().trim();
        productSupplierPhone = extractNumberFromFormat(productSupplierPhoneNumberEditText.getText().toString().trim());
        String priceString = productPriceEditText.getText().toString().trim();
        productPrice = compilePriceString(priceString);
        String quantityString = productQuantityEditText.getText().toString().trim();
        productQuantity = Integer.parseInt(compileQuantityString(quantityString));
        String isbn13 = productISBN13EditText.getText().toString().trim();
        String isbn10 = productISBN10EditText.getText().toString().trim();
        createToast("ISBN 13: " + isbn13);
        if (!isEmpty(isbn13)) {
            productISBN13 = String.valueOf(extractNumberFromFormat(isbn13));
        } else {
            productISBN13 = String.valueOf(zero);
        }
        if (!isEmpty(isbn10)) {
            productISBN10 = String.valueOf(extractNumberFromFormat(isbn10));
        } else {
            productISBN10 = String.valueOf(zero);
        }
    }

    private String extractNumberFromFormat(String value) {
        String currentString = value;
        currentString = currentString.replace("-", "");
        currentString = currentString.replace("(", "");
        currentString = currentString.replace(")", "");
        currentString = currentString.replace(" ", "");
        return currentString;
    }

    private double compilePriceString(String price) {
        DecimalFormat format = new DecimalFormat("0.00");
        if (!isEmpty(price)) {
            return Double.valueOf(format.format(Double.parseDouble(price)));
        } else {
            return Double.valueOf(format.format(zero));
        }
    }

    private String compileQuantityString(String quantity) {
        if (TextUtils.isEmpty(quantity)) {
            quantity = String.valueOf(zero);
        }
        return quantity;
    }

    private void initImplementContentValues(ContentValues userInputValues) {
        if (isSanityCheckGood()) {
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_NAME, productName);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_AUTHOR, productAuthor);
            createToast("initImplement: " + productISBN13);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_ISBN_13, productISBN13);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_ISBN_10, productISBN10);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, productSupplierName);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhone);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_PRICE, productPrice);
            Log.i(LOG_TAG, "Product Price: " + productPrice);
            implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        }
    }

    private boolean isSanityCheckGood() {
//        if (!isEmpty(productName)
//                && !isEmpty(productAuthor)
//                && )
        return true;
    }

    private void implementContentValues(ContentValues contentValues, String key, Object userInput) {
        if (userInput instanceof String) {
            if (!TextUtils.isEmpty((String) userInput)) {
                contentValues.put(key, (String) userInput);
            }
        } else if (userInput instanceof Integer) {
            if ((Integer) userInput >= 0) {
                contentValues.put(key, (Integer) userInput);
            }
        } else if (userInput instanceof Double) {
            if ((Double) userInput >= 0) {
                contentValues.put(key, (Double) userInput);
            }
        }
    }

    private void updateProduct(Uri uri, ContentValues userValues) {
        int updatedRows = getContentResolver().update(uri, userValues, null, null);
        if (updatedRows > zero) {
            createToast(context.getResources().getString(R.string.successful_data_updated));
            finish();
        } else {
            throw new IllegalArgumentException(context.getResources().getString(R.string.illegal_argument_exception_unsuccesful_save));
        }
    }

    private void insertProduct(Uri uri, ContentValues userValues) {
        Uri newURI = getContentResolver().insert(uri, userValues);
        if (newURI != null) {
            createToast(context.getResources().getString(R.string.successful_data_saved));
            finish();
        } else {
            createToast(context.getResources().getString(R.string.unsuccessful_data_saved));
        }
    }

    private void deleteProduct(Uri uri, String selection, String[] selectionArgs) {
        int deletedRows = getContentResolver().delete(uri, selection, selectionArgs);
    }

    private void createToast(String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (!mHasBeenTouched) {              // if no changes were made, go back...
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardProgressDialogButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelProgress();
            }
        };
        showUnsavedChangesDialog(discardProgressDialogButtonClickListener);
    }

    private void deleteProductDialog(DialogInterface.OnClickListener deleteProductDialogButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_selected_product_dialog);
        builder.setPositiveButton(R.string.delete_selected_product, deleteProductDialogButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardProgressDialogButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_progress_dialog);
        builder.setPositiveButton(R.string.discard, discardProgressDialogButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clearInputFields() {
        RelativeLayout parentGroup = findViewById(R.id.parent_view_group);
        for (int i = 0; i < parentGroup.getChildCount(); i++) {
            View currentChild = parentGroup.getChildAt(i);
            if (currentChild instanceof EditText) {
                ((EditText) currentChild).setText(null);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID) {
            case CURSOR_LOADER_ID:
                String[] projection = {
                        BookEntry.COLUMN_PRODUCT_NAME,
                        BookEntry.COLUMN_PRODUCT_AUTHOR,
                        BookEntry.COLUMN_PRODUCT_ISBN_13,
                        BookEntry.COLUMN_PRODUCT_ISBN_10,
                        BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                        BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
                        BookEntry.COLUMN_PRODUCT_PRICE,
                        BookEntry.COLUMN_PRODUCT_QUANTITY
                };
                return new CursorLoader(getApplicationContext(), mSelectedURI, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && mSelectedURI != null) {
            updateUI(cursor);
        } else {
            mCurrentQuantity = 0;
            productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
        }
        initButtonOnClickListeners();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearInputFields();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout_editor, menu);
        if (mSelectedURI == null) {
            MenuItem deleteMenuItem = (MenuItem) menu.findItem(R.id.action_delete_product);
            deleteMenuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_product:
                DialogInterface.OnClickListener deleteProductDialogButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct(mSelectedURI, null, null);
                        finish();
                    }
                };
                deleteProductDialog(deleteProductDialogButtonClickListener);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}