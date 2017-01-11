package com.example.demahum.linuxutilities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class configuration_list extends ActionBarActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    ListView listView ;

    public String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_list);

        String origin = getIntent().getStringExtra("origin");
        getSupportActionBar().setTitle("Stored configurations");


        if (origin.matches("new")){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Configuration stored successfully.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        listView = (ListView) findViewById(R.id.configurations);
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT name FROM hosts;";
        final SQLiteDatabase db = openOrCreateDatabase("conf", MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    labels.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }else{
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No configurations found.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            cursor.close();
            db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, labels);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = "";
                String ip = "";
                String port = "";
                String username = "";
                String password = "";

                String itemValue = (String)listView.getItemAtPosition(position);
                final SQLiteDatabase db = openOrCreateDatabase("conf", MODE_PRIVATE, null);
                Cursor cursor = db.rawQuery("select * from hosts where name = '" + itemValue + "';", null);
                if (cursor.moveToFirst()) {
                    do {
                        name = cursor.getString(0);
                        ip = cursor.getString(1);
                        port = cursor.getString(2);
                        username = cursor.getString(3);
                        password = cursor.getString(4);
                    } while (cursor.moveToNext());
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sorry, something went wrong with the app. Please, report to the author.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                cursor.close();
                db.close();

                Intent intent = new Intent(getBaseContext(), execute_command.class);
                intent.putExtra("name", name);
                intent.putExtra("ip", ip);
                intent.putExtra("port", port);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

    }

    private void addDrawerItems() {
        String[] osArray = {"Configurations", "New Configuration", "Execute command"};
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
                else if (position == 2){
                    Intent intent = new Intent(getBaseContext(), execute_command.class);
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
