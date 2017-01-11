package com.example.demahum.linuxutilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.input.InputManager;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class execute_command extends AppCompatActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    public String output;
    public String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_command);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle("Execute command");
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Button configuration = (Button)findViewById(R.id.button);


        final SQLiteDatabase db = openOrCreateDatabase("conf", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS hosts (name text, host text, port text, username text, password text, primary key(name));");
        Cursor cursor = db.rawQuery("SELECT name FROM hosts limit 1;", null);
        if (cursor.moveToFirst()) {
            do {
                configuration.setText(cursor.getString(0));
                configuration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), configuration_list.class);
                        intent.putExtra("origin", "old");
                        startActivity(intent);
                    }
                });
            } while (cursor.moveToNext());
        }else{
                configuration.setText(R.string.add_config);
                configuration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), store_configuration.class);
                        startActivity(intent);
                    }
                });
        }
        cursor.close();
        db.close();
        final TextView response = (TextView)findViewById(R.id.textView);
        final Button execute = (Button)findViewById(R.id.button2);
        final EditText editText = (EditText)findViewById(R.id.editText);
        final Button clear = (Button)findViewById(R.id.button3);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                editText.setText("");
            }
                                 });

        output = "empty";
        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                command = editText.getText().toString();
                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                             output = executeRemoteCommand("muhamed", "secured","192.168.2.31", 22, command);
                             this.publishProgress();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    protected void onProgressUpdate(Void...values) {
                        response.setText(output);
                    }



                }.execute(1);
            }

        });

        response.setText(output);



    }

    public static String executeRemoteCommand(String username,String password,String hostname,int port, String command)
            throws Exception {

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");

        InputStream inputStream = channelssh.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // Execute command
        channelssh.setCommand(command);
        channelssh.connect();

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append('\n');
        }
        channelssh.disconnect();
        return stringBuilder.toString();
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

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Execute command");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}