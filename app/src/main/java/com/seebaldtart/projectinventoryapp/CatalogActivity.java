package com.seebaldtart.projectinventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.seebaldtart.projectinventoryapp.data.InventoryContract;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.Tools;
import com.seebaldtart.projectinventoryapp.data.ProductCursorAdapter;
import com.seebaldtart.projectinventoryapp.data.ProductDBHelper;
import com.seebaldtart.projectinventoryapp.data.InventoryContract.BookEntry;

public class CatalogActivity extends AppCompatActivity {
    private ListView listView;
    private TextView emptyText;
    private CursorAdapter cursorAdapter;
    private ProductDBHelper mDBHelper;
    private final int addProduct = R.id.action_add_product;
    private final int editProduct = R.id.action_edit_product;
    private final int removeProduct = R.id.action_remove_product;
    private final int deleteProducts = R.id.action_delete_all_products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        mDBHelper = new ProductDBHelper(this);
        setupUI();
    }

    private void setupUI() {           // Read Database information
        setUpViews();
    }

    private void setUpViews() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditorActivity(null);
            }
        });
        listView = findViewById(R.id.list);
        emptyText = findViewById(R.id.empty_view);
        cursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        listView.setEmptyView(emptyText);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri selectedURI = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                startEditorActivity(selectedURI);
            }
        });
    }

    private void startEditorActivity(Uri selectedURI) {
        Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
        intent.setData(selectedURI);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        setupUI();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_product:
                startEditorActivity(null);
            case R.id.action_delete_all_products:
                // TODO: Show Delete Dialog
        }
        return true;
    }
}