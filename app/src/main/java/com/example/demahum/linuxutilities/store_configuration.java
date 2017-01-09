package com.example.demahum.linuxutilities;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class store_configuration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Store new configuration");
        final SQLiteDatabase db = openOrCreateDatabase("conf", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS hosts (name text, host text, port text, username text, password text, primary key(name));");

        //SQLiteDatabase db = dbHelper.getWritableDatabase();

        final EditText confName = (EditText) (findViewById(R.id.confName));
        final EditText hostIP = (EditText) (findViewById(R.id.host_ip));
        final EditText port = (EditText) (findViewById(R.id.port));
        final EditText username = (EditText) (findViewById(R.id.username));
        final EditText password = (EditText) (findViewById(R.id.password));
        final Button saveButton = (Button) (findViewById(R.id.saveButton));


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confName_string = confName.getText().toString();
                String host_string = hostIP.getText().toString();
                String port_string = port.getText().toString();
                String username_string = username.getText().toString();
                String password_string = password.getText().toString();

                SQLiteStatement stmt = db.compileStatement("INSERT INTO hosts values (?, ?, ?, ?, ?)");
                stmt.bindString(1, confName_string);
                stmt.bindString(2, host_string);
                stmt.bindString(3, port_string);
                stmt.bindString(4, username_string);
                stmt.bindString(5, password_string);
                try{
                    stmt.execute();
                }catch (android.database.sqlite.SQLiteConstraintException e){
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Configuration name already exists!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
