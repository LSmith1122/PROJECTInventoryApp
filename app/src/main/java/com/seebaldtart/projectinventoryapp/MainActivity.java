package com.seebaldtart.projectinventoryapp;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.text_view);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(intent);
            }
        });
    }
    private void updateUI() {           // Read Database information

    }
    @Override
    public void onOptionsMenuClosed(Menu menu) {            //
        super.onOptionsMenuClosed(menu);
    }
    @Override
    protected void onStart() {
        // TODO: Update the UI with updated/current database information before executing super.onStart()...
        super.onStart();
    }
}