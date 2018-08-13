package com.seebaldtart.projectinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;
import com.seebaldtart.projectinventoryapp.data.ProductCursorAdapter;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int zero = 0;
    private final int ASYNC_LOADER_ID = 0;
    private final int CURSOR_LOADER_ID = 1;
    private ListView listView;
    private LinearLayout emptyGroup;
    private LinearLayout loadingGroup;
    private CursorAdapter cursorAdapter;
    private Uri mSelectedURI;
    private Cursor mCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        setTitle(R.string.catalog_label_books);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditorActivity(null);
            }
        });
        setupViews();
        setupListViewListeners();
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(CURSOR_LOADER_ID, null, this).forceLoad();
    }

    private void setupListViewListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedURI = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                if (mCursor != null) {
                    switch (view.getId()) {
                        case R.id.parent_view_group:
                            startEditorActivity(mSelectedURI);
                            break;
                        default:
                            Log.i("TEST", "Unknown ListView item...");
                            createToast("Error");
                            break;
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                Log.i("TEST", "Long Clicked item with ID: " + id);
                DialogInterface.OnClickListener deleteButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri selectedURI = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                        int deleteRows = getContentResolver().delete(selectedURI, null, null);
                        if (deleteRows > zero) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.successful_data_deletion_selected), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.unsuccessful_data_deletion) + ", deleted " + deleteRows + " rows", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                deleteProductDialog(deleteButtonClickListener);
                return true;
            }
        });
    }

    private void setupViews() {
        listView = (ListView) findViewById(R.id.list);
        emptyGroup = (LinearLayout) findViewById(R.id.empty_group);
        loadingGroup = (LinearLayout) findViewById(R.id.loading_group);
        cursorAdapter = new ProductCursorAdapter(this, null);
        emptyGroup.setVisibility(View.GONE);
        listView.setAdapter(cursorAdapter);
        listView.setEmptyView(loadingGroup);
    }

    private void startEditorActivity(Uri selectedURI) {
        Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
        intent.setData(selectedURI);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_product:
                startEditorActivity(null);
                return true;
            case R.id.action_delete_all_products:
                DialogInterface.OnClickListener deleteButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int deleteRows = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                        if (deleteRows > zero) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.successful_data_deletion_all), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.unsuccessful_data_deletion) + ", deleted " + deleteRows + " rows", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                deleteAllProductsDialog(deleteButtonClickListener);
        }
        return super.onOptionsItemSelected(item);
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

    private void deleteAllProductsDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_product_dialog);
        builder.setPositiveButton(R.string.delete_all_products, deleteButtonClickListener);
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

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        listView.setEmptyView(loadingGroup);
        switch (loaderID) {
            case CURSOR_LOADER_ID:
                String[] projection = {
                        BookEntry._ID,
                        BookEntry.COLUMN_PRODUCT_NAME,
                        BookEntry.COLUMN_PRODUCT_AUTHOR,
                        BookEntry.COLUMN_PRODUCT_ISBN_13,
                        BookEntry.COLUMN_PRODUCT_ISBN_10,
                        BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                        BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER,
                        BookEntry.COLUMN_PRODUCT_PRICE,
                        BookEntry.COLUMN_PRODUCT_QUANTITY};
                return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
            case ASYNC_LOADER_ID:
                // Return null unless if planning to create an ASYNCTask, preferably for JSON Operations
                // If planning to create ASYNCTask, write code that does something...
                return null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
        mCursor = cursor;
        if (cursor.getCount() == zero) {
            Log.i("TEST", "Cursor is empty");
            listView.setEmptyView(emptyGroup);
            loadingGroup.setVisibility(View.GONE);
        } else {
            listView.setEmptyView(loadingGroup);
            emptyGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void createToast(String messageString) {
        Toast.makeText(getApplicationContext(), messageString, Toast.LENGTH_SHORT).show();
    }
}