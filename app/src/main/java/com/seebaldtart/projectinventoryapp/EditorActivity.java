package com.seebaldtart.projectinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

import java.text.DecimalFormat;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int zero = 0;
    private final int CURSOR_LOADER_ID = 1;
    private String LOG_TAG;
    private TextView descriptionText;
    private EditText productNameEditText;
    private EditText productAuthorEditText;
    private EditText productISBN13EditText;
    private EditText productISBN10EditText;
    private EditText productSupplierNameEditText;
    private EditText productSupplierPhoneNumberEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText multiplePriceEditText;
    private EditText multipleQuantityEditText;
    private ImageButton productQuantityButtonIncrement;
    private ImageButton productQuantityButtonDecrement;
    private ImageButton priceQuantityButtonIncrement;
    private ImageButton priceQuantityButtonDecrement;
    private Button orderButton;
    private FloatingActionButton saveButton;
    private FloatingActionButton cancelButton;
    private boolean mHasBeenTouched = false;
    private Uri mSelectedURI = null;
    private Context context;

    /*
    Initial variables for the selected item
    */
    private String initName;
    private String initAuthor;
    private String initISBN13;
    private String initISBN10;
    private String initSupplierName;
    private String initSupplierPhone;
    private Double initPrice;
    private int initQuantity;

    /*
    Variables used for updating selected item (User Input)
    */
    private String userProductName;
    private String userProductAuthor;
    private String userProductISBN13;
    private String userProductISBN10;
    private String userProductSupplierName;
    private String userProductSupplierPhone;
    private double userProductPrice;
    private int userProductQuantity;

    /*
    Variables used solely for keeping track of User Input and updating EditTexts only
    */
    private int mCurrentQuantity;
    private double mCurrentPrice;
    private int maxCVAmount = 0;
    private int actualCVAmount;
    private final int minMultiple = 1;

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
            productPriceEditText.setText(createTextForPrice(zero));
            productQuantityEditText.setText(String.valueOf(zero));
            initButtonOnClickListeners();
        }
    }

    private void initButtonOnClickListeners() {
        multipleQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(multipleQuantityEditText.getText())) {
                    multipleQuantityEditText.setText(String.valueOf(minMultiple));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        multiplePriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(multiplePriceEditText.getText())) {
                    multiplePriceEditText.setText(String.valueOf(minMultiple));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productQuantityButtonIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuantity = getQuantityValue();
                mCurrentQuantity = mCurrentQuantity + Integer.valueOf(multipleQuantityEditText.getText().toString());
                productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
                productQuantityEditText.setSelection(productQuantityEditText.getText().length());
            }
        });
        productQuantityButtonDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuantity = getQuantityValue();
                mCurrentQuantity = mCurrentQuantity - Integer.valueOf(multipleQuantityEditText.getText().toString());
                if (mCurrentQuantity < 0) {
                    mCurrentQuantity = 0;
                }
                productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
                productQuantityEditText.setSelection(productQuantityEditText.getText().length());
            }
        });
        priceQuantityButtonIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPrice = getPriceValue();
                mCurrentPrice = mCurrentPrice + Integer.valueOf(multiplePriceEditText.getText().toString());
                productPriceEditText.setText(createTextForPrice(mCurrentPrice));
                productPriceEditText.setSelection(productPriceEditText.getText().length());
            }
        });
        priceQuantityButtonDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPrice = getPriceValue();
                mCurrentPrice = mCurrentPrice - Integer.valueOf(multiplePriceEditText.getText().toString());
                if (mCurrentPrice < 0) {
                    mCurrentPrice = 0;
                }
                productPriceEditText.setText(createTextForPrice(mCurrentPrice));
                productPriceEditText.setSelection(productPriceEditText.getText().length());
            }
        });
    }

    private int getQuantityValue() {
        String value = productQuantityEditText.getText().toString();
        if (!TextUtils.isEmpty(value)) {
            return Integer.parseInt(value);
        } else {
            return zero;
        }
    }

    private double getPriceValue() {
        return Double.parseDouble(productPriceEditText.getText().toString());
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
                cursor.moveToLast();
                int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME);
                int authorColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_AUTHOR);
                int isbn13ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_13);
                int isbn10ColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_ISBN_10);
                int supplierNameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
                int priceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY);
                initName = cursor.getString(nameColumnIndex);
                initAuthor = cursor.getString(authorColumnIndex);
                initISBN13 = cursor.getString(isbn13ColumnIndex);
                initISBN10 = cursor.getString(isbn10ColumnIndex);
                initSupplierName = cursor.getString(supplierNameColumnIndex);
                initSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
                initPrice = cursor.getDouble(priceColumnIndex);
                mCurrentPrice = initPrice;
                initQuantity = cursor.getInt(quantityColumnIndex);
                mCurrentQuantity = initQuantity;
                productNameEditText.setText(initName);
                productAuthorEditText.setText(initAuthor);
                productISBN13EditText.setText(compileISBNNumber(initISBN13, BookEntry.MAX_ISBN_13));
                productISBN10EditText.setText(compileISBNNumber(initISBN10, BookEntry.MAX_ISBN_10));
                productSupplierNameEditText.setText(initSupplierName);
                compileSupplierPhoneNumber(initSupplierPhone);
                productPriceEditText.setText(createTextForPrice(initPrice));
                productQuantityEditText.setText(String.valueOf(initQuantity));
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
        priceQuantityButtonIncrement = findViewById(R.id.product_price_increment_button);
        priceQuantityButtonDecrement = findViewById(R.id.product_price_decrement_button);
        saveButton = findViewById(R.id.save_product_button);
        cancelButton = findViewById(R.id.cancel_button);
        orderButton = findViewById(R.id.order_button);
        multiplePriceEditText = findViewById(R.id.product_price_multiple_edit_text);
        multipleQuantityEditText = findViewById(R.id.product_quantity_multiple_edit_text);
        multipleQuantityEditText.setText(String.valueOf(minMultiple));
        multiplePriceEditText.setText(String.valueOf(minMultiple));
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
        if (mSelectedURI == null) {
            orderButton.setVisibility(View.GONE);
        } else {
            orderButton.setVisibility(View.VISIBLE);
        }
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
            int mSwitch = zero;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productSupplierPhoneNumberEditText.length();
                if (mSwitch == 1) {
                    if (length > zero) {
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
            int mSwitch = zero;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productISBN13EditText.length();
                if (mSwitch == 1) {
                    if (length > zero) {
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
            int mSwitch = zero;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productISBN10EditText.length();
                if (mSwitch == 1) {
                    if (length > zero) {
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
        productPriceEditText.addTextChangedListener(new TextWatcher() {
            int mSwitch = zero;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productPriceEditText.length();
                if (mSwitch == 1) {
                    if (length == zero) {
                        productPriceEditText.removeTextChangedListener(this);
                        productPriceEditText.setText(createTextForPrice(zero));
                        productPriceEditText.setSelection(productPriceEditText.getText().length());
                        productPriceEditText.addTextChangedListener(this);
                    }
                } else {
                    mSwitch++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productQuantityEditText.addTextChangedListener(new TextWatcher() {
            int mSwitch = zero;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = productQuantityEditText.length();
                if (mSwitch == 1) {
                    if (length == zero) {
                        productQuantityEditText.removeTextChangedListener(this);
                        productQuantityEditText.setText(String.valueOf(zero));
                        productQuantityEditText.setSelection(productQuantityEditText.getText().length());
                        productQuantityEditText.addTextChangedListener(this);
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
        String emptySpace = " ";
        String country = "+";
        String startAreaCode = "(";
        String endAreaCode = ")";
        String dash = "-";
        String currentString = value;
        currentString = extractNumberFromFormat(currentString);
        String beginning, phoneNumberString = "";
        if (!TextUtils.isEmpty(currentString)) {
            phoneNumberString = currentString;
            if (currentString.length() > 3) {
                beginning = startAreaCode + currentString.substring(0, 3) + endAreaCode + emptySpace;
                phoneNumberString = beginning + currentString.substring(3);
                if (currentString.length() > 6) {
                    phoneNumberString = beginning + currentString.substring(3, 6) + dash + currentString.substring(6);
                    if (currentString.length() > 10) {
                        phoneNumberString = country + currentString.substring(0, 1) + emptySpace
                                + startAreaCode + currentString.substring(1, 4) + endAreaCode + emptySpace
                                + currentString.substring(4, 7)
                                + dash
                                + currentString.substring(7);
                        if (currentString.length() > 11) {
                            phoneNumberString = country + currentString.substring(0, 2) + emptySpace
                                    + startAreaCode + currentString.substring(2, 5) + endAreaCode + emptySpace
                                    + currentString.substring(5, 8)
                                    + dash
                                    + currentString.substring(8);
                            if (currentString.length() > 12) {
                                phoneNumberString = country + currentString.substring(0, 3) + emptySpace
                                        + startAreaCode + currentString.substring(3, 6) + endAreaCode + emptySpace
                                        + currentString.substring(6, 9)
                                        + dash
                                        + currentString.substring(9);
                            }
                        }
                    }
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
                        ISBNString = first + currentString.substring(3);
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
                        ISBNString = first + currentString.substring(1);
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
        if (initImplementContentValues(userInputValues)) {
            if (mSelectedURI != null) {             // User has selected a specific product to update...
                updateProduct(mSelectedURI, userInputValues);
            } else {                                // User is adding a new product...
                insertProduct(BookEntry.CONTENT_URI, userInputValues);
            }
        } else {
            createToast(context.getResources().getString(R.string.update_provide_attributes));
        }
    }

    private void recordUserInput() {
        userProductName = productNameEditText.getText().toString().trim();
        userProductAuthor = productAuthorEditText.getText().toString().trim();
        userProductSupplierName = productSupplierNameEditText.getText().toString().trim();
        userProductSupplierPhone = extractNumberFromFormat(productSupplierPhoneNumberEditText.getText().toString().trim());
        userProductPrice = Double.parseDouble(parsePrice(productPriceEditText.getText().toString().trim()));
        userProductQuantity = Integer.parseInt(compileQuantityString(productQuantityEditText.getText().toString().trim()));
        userProductISBN13 = productISBN13EditText.getText().toString().trim();
        userProductISBN10 = productISBN10EditText.getText().toString().trim();
        if (!isEmpty(userProductISBN13)) {
            userProductISBN13 = String.valueOf(extractNumberFromFormat(userProductISBN13));
        } else {
            userProductISBN13 = String.valueOf(zero);
        }
        if (!isEmpty(userProductISBN10)) {
            userProductISBN10 = String.valueOf(extractNumberFromFormat(userProductISBN10));
        } else {
            userProductISBN10 = String.valueOf(zero);
        }
    }

    private String parsePrice(String priceString) {
        /*
        Thanks to kshetline for this code snippet inspiration:
        https://stackoverflow.com/questions/49741511/convert-multiple-point-number-to-a-single-point-double
        */
        String ending = "";
        int lastDot = priceString.lastIndexOf('.');
        if (lastDot >= 0) {
            ending = priceString.substring(lastDot);
            priceString = priceString.substring(0, lastDot).replaceAll("\\.", "");
        }
        return priceString + ending;
    }

    private String extractNumberFromFormat(String value) {
        String currentString = value;
        currentString = currentString.replace("+", "");
        currentString = currentString.replace("-", "");
        currentString = currentString.replace("(", "");
        currentString = currentString.replace(")", "");
        currentString = currentString.replace(" ", "");
        return currentString;
    }

    private String compileQuantityString(String quantity) {
        if (TextUtils.isEmpty(quantity)) {
            quantity = String.valueOf(zero);
        }
        return quantity;
    }

    private boolean initImplementContentValues(ContentValues userInputValues) {
        maxCVAmount = 0;
        actualCVAmount = 0;
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_NAME, userProductName, true);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_AUTHOR, userProductAuthor, true);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_ISBN_13, userProductISBN13, false);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_ISBN_10, userProductISBN10, false);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, userProductSupplierName, true);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, userProductSupplierPhone, true);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_PRICE, userProductPrice, true);
        actualCVAmount = actualCVAmount + implementContentValues(userInputValues, BookEntry.COLUMN_PRODUCT_QUANTITY, userProductQuantity, true);
        return actualCVAmount >= maxCVAmount;
    }

    private int implementContentValues(ContentValues contentValues, String key, Object userInput, boolean required) {
        maxCVAmount++;
        int mPass = 1;
        int mFail = 0;
        if (required) {
            if (checkContentValuesStatus(contentValues, key, userInput)) {               // if it's required AND not empty... pass with 1
                return mPass;
            } else {                                        // if it's required, but empty... fail with 0
                return mFail;
            }
        } else {                                            // automatically pass with 1
            checkContentValuesStatus(contentValues, key, userInput);
            return mPass;
        }
    }

    private boolean checkContentValuesStatus(ContentValues contentValues, String key, Object userInput) {
        if (userInput instanceof String) {
            if (!TextUtils.isEmpty((String) userInput)) {
                contentValues.put(key, (String) userInput);
                return true;
            } else {
                return false;
            }
        } else if (userInput instanceof Integer) {
            if ((Integer) userInput >= 0) {
                contentValues.put(key, (Integer) userInput);
                return true;
            } else {
                return false;
            }
        } else if (userInput instanceof Double) {
            if ((Double) userInput >= 0) {
                contentValues.put(key, (Double) userInput);
                return true;
            } else {
                return false;
            }
        }
        return false;
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
        getContentResolver().delete(uri, selection, selectionArgs);
    }

    private void createToast(String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (!mHasBeenTouched) {              // if no changes were made, go back...
            Log.i("TEST", "Nothing has been pressed...");
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
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        if (cursor != null && mSelectedURI != null) {
            orderButton.setVisibility(View.VISIBLE);
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (initQuantity > 0) {
                        updateQuantity(context, initQuantity, ContentUris.parseId(mSelectedURI));           // Decrease quantity in database
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + initSupplierPhone));
                        context.startActivity(intent);
                    } else {                        // Quantity is too low, notify User
                        createToast(context.getResources().getString(R.string.no_products_in_inventory) + "\nwith quantity of " + initQuantity);
                    }
                }
            });
            updateUI(cursor);
        } else {
            orderButton.setVisibility(View.GONE);
            mCurrentQuantity = 0;
            productQuantityEditText.setText(String.valueOf(mCurrentQuantity));
            mCurrentPrice = 0;
            productPriceEditText.setText(createTextForPrice(mCurrentPrice));
        }
        initButtonOnClickListeners();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearInputFields();
    }

    private void updateQuantity(Context context, int currentQuantity, long id) {
        int newQuantity = currentQuantity - 1;
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
        Uri mSelectedURI = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
        int updatedRowID = context.getContentResolver().update(mSelectedURI, values, null, null);
        if (updatedRowID > zero) {
            createToast(context.getResources().getString(R.string.successful_product_sale));
        } else {
            createToast(context.getResources().getString(R.string.unsuccessful_product_sale_update));
        }
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