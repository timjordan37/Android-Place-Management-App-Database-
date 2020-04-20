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
 * Purpose: To display a location description
 *
 * @author Tim Jordan   mailto:tsjorda1@asu.edu
 * @version November 2019
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;

public class LocationDetailActivity extends AppCompatActivity {

    private TextView nameText;
    private TextView descriptionText;
    private TextView categoryText;
    private TextView addressTitleText;
    private TextView addressStreetText;
    private TextView elevationText;
    private TextView latitudeText;
    private TextView longitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        nameText = findViewById(R.id.nameTV);
        descriptionText = findViewById(R.id.descTV);
        categoryText = findViewById(R.id.catTV);
        addressTitleText = findViewById(R.id.addTTV);
        addressStreetText = findViewById(R.id.addSTV);
        elevationText = findViewById(R.id.elevTV);
        latitudeText = findViewById(R.id.latTV);
        longitudeText = findViewById(R.id.longTV);
        Intent intent = getIntent();
        String locName = intent.getStringExtra("name");

        try {
            LocationDB db = new LocationDB(this);
            SQLiteDatabase locDB = db.openDB();
            Cursor cur = locDB.rawQuery("SELECT * FROM locations where name=? ;",
                    new String[]{locName});
            String description = "unknown";
            String category = "unknown";
            String addressT = "unknown";
            String addressS = "unknown";
            String elevation = "0.00";
            String latitude = "0.00";
            String longitude = "0.00";
            while (cur.moveToNext()) {
                description = cur.getString(1);
                category = cur.getString(2);
                addressT = cur.getString(3);
                addressS = cur.getString(4);
                elevation = Double.toString(cur.getDouble(5));
                latitude = Double.toString(cur.getDouble(6));
                longitude = Double.toString(cur.getDouble(7));
            }
            nameText.setText(locName);
            descriptionText.setText(description);
            categoryText.setText(category);
            addressTitleText.setText(addressT);
            addressStreetText.setText(addressS);
            elevationText.setText(elevation);
            latitudeText.setText(latitude);
            longitudeText.setText(longitude);
            cur.close();
            locDB.close();
            db.close();
        } catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "Exception getting location info: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_detail_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.detail_edit:
                String locationName = nameText.getText().toString();
                Intent i = new Intent(this, EditActivity.class);
                i.putExtra("name", locationName);
                startActivity(i);
                return true;
            case R.id.detail_delete:
                String locationNameDel = nameText.getText().toString();
                Intent j = new Intent(this, DeleteActivity.class);
                j.putExtra("name", locationNameDel);
                startActivity(j);
                LocationDetailActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        nameText = findViewById(R.id.nameTV);
        descriptionText = findViewById(R.id.descTV);
        categoryText = findViewById(R.id.catTV);
        addressTitleText = findViewById(R.id.addTTV);
        addressStreetText = findViewById(R.id.addSTV);
        elevationText = findViewById(R.id.elevTV);
        latitudeText = findViewById(R.id.latTV);
        longitudeText = findViewById(R.id.longTV);
        Intent intent = getIntent();
        String locName = intent.getStringExtra("name");

        try {
            LocationDB db = new LocationDB(this);
            SQLiteDatabase locDB = db.openDB();
            Cursor cur = locDB.rawQuery("SELECT * FROM locations where name=? ;",
                    new String[]{locName});
            String description = "unknown";
            String category = "unknown";
            String addressT = "unknown";
            String addressS = "unknown";
            String elevation = "0.00";
            String latitude = "0.00";
            String longitude = "0.00";
            while (cur.moveToNext()) {
                description = cur.getString(1);
                category = cur.getString(2);
                addressT = cur.getString(3);
                addressS = cur.getString(4);
                elevation = Double.toString(cur.getDouble(5));
                latitude = Double.toString(cur.getDouble(6));
                longitude = Double.toString(cur.getDouble(7));
            }
            nameText.setText(locName);
            descriptionText.setText(description);
            categoryText.setText(category);
            addressTitleText.setText(addressT);
            addressStreetText.setText(addressS);
            elevationText.setText(elevation);
            latitudeText.setText(latitude);
            longitudeText.setText(longitude);
            cur.close();
            locDB.close();
            db.close();
        } catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "Exception getting location info: " + e.getMessage());
        }
    }
}
