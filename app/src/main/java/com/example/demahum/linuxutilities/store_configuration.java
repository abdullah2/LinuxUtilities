package com.example.demahum.linuxutilities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class store_configuration extends ActionBarActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_configuration);

        getSupportActionBar().setTitle("Store new configuration");

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        final SQLiteDatabase db = openOrCreateDatabase("conf", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS hosts (name text, host text, port text, username text, password text, primary key(name));");

        //TO BE REMOVED
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

                if (confName_string.matches("") || host_string.matches("") || port_string.matches("") || username_string.matches("") || password_string.matches("")) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Fill in all of the fields!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    SQLiteStatement stmt = db.compileStatement("INSERT INTO hosts values (?, ?, ?, ?, ?)");
                    stmt.bindString(1, confName_string);
                    stmt.bindString(2, host_string);
                    stmt.bindString(3, port_string);
                    stmt.bindString(4, username_string);
                    stmt.bindString(5, password_string);
                    try{
                        stmt.execute();
                        Intent intent = new Intent(getBaseContext(), configuration_list.class);
                        intent.putExtra("origin", "new");
                        startActivity(intent);
                    }catch (android.database.sqlite.SQLiteConstraintException e){
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Configuration name already exists!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        });
    }

    private void addDrawerItems() {
        String[] osArray = {"Configurations", "New Configuration"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if (position == 0) {
                    Intent intent = new Intent(getBaseContext(), configuration_list.class);
                    intent.putExtra("origin", "old");
                    startActivity(intent);
                }
                else if (position == 1){
                    Intent intent = new Intent(getBaseContext(), store_configuration.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Stored configurations");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
