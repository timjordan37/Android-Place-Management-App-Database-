package edu.asu.bsse.tsjorda1.plactester;

/*
 * Copyright 2019 Tim Jordan,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: To start up the database and populate a list view of location names
 *
 * @author Tim Jordan   mailto:tsjorda1@asu.edu
 * @version November 2019
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import android.content.Intent;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView locLV;
    private String[] locs;
    private ArrayAdapter<String> locAdapter;
    private LocationDB db;
    private EditText locNameET;
    private SearchView searchView;
    private String searchString;
    private SQLiteDatabase locDB;
    private Cursor cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locLV = findViewById(R.id.locLV);
        try {
            db = new LocationDB(this);
            locDB = db.openDB();
            cur = locDB.rawQuery("SELECT name FROM locations;", new String[]{});
            ArrayList<String> al = new ArrayList<>();
            while(cur.moveToNext()) {
                try {
                    al.add(cur.getString(0));
                }catch (Exception e) {
                    android.util.Log.w(this.getClass().getSimpleName(), "exception stepping through cursor "+e.getMessage());
                }
            }
            locs = al.toArray(new String[al.size()]);
            locAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,locs);
            locLV.setAdapter(locAdapter);
            locLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = ((TextView)view).getText().toString();
                    Intent i = new Intent(view.getContext(), LocationDetailActivity.class);
                    i.putExtra("name", item);
                    startActivityForResult(i, 0);
                }
            });
        }catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "unable to setup location list view");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_routes:
                Intent i = new Intent(this, CalculationsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_add_new:
                Intent j = new Intent(this,AddNewActivity.class);
                startActivity(j);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        locLV = findViewById(R.id.locLV);
        try {
            db = new LocationDB(this);
            locDB = db.openDB();
            cur = locDB.rawQuery("SELECT name FROM locations;", new String[]{});
            ArrayList<String> al = new ArrayList<>();
            while(cur.moveToNext()) {
                try {
                    al.add(cur.getString(0));
                }catch (Exception e) {
                    android.util.Log.w(this.getClass().getSimpleName(), "exception stepping through cursor "+e.getMessage());
                }
            }
            locs = al.toArray(new String[al.size()]);
            locAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,locs);
            locLV.setAdapter(locAdapter);
            locLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = ((TextView)view).getText().toString();
                    Intent i = new Intent(view.getContext(), LocationDetailActivity.class);
                    i.putExtra("name", item);
                    startActivityForResult(i, 0);
                }
            });
        }catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "unable to setup location list view");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cur.close();
        locDB.close();
    }

    public boolean onQueryTextChange(String query) {
        return false;
    }

    public boolean onQueryTextSubmit(String query){
        this.searchString = query;
        searchView.clearFocus();
        return false;
    }
}
