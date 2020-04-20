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
 * Purpose: To edit an existing location
 *
 * @author Tim Jordan   mailto:tsjorda1@asu.edu
 * @version November 2019
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    private TextView nameText;
    private EditText descriptionText;
    private EditText categoryText;
    private EditText addressTitleText;
    private EditText addressStreetText;
    private EditText elevationText;
    private EditText latitudeText;
    private EditText longitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        nameText = findViewById(R.id.nameTV);
        descriptionText = findViewById(R.id.descET);
        categoryText = findViewById(R.id.catET);
        addressTitleText = findViewById(R.id.addTET);
        addressStreetText = findViewById(R.id.addSET);
        elevationText = findViewById(R.id.elevET);
        latitudeText = findViewById(R.id.latET);
        longitudeText = findViewById(R.id.longET);
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

    public void editLocation(View v) {

        String name = nameText.getText().toString();

        try {
            LocationDB db = new LocationDB(this);
            SQLiteDatabase locDB = db.openDB();
            ContentValues cv = new ContentValues();
            cv.put("name",this.nameText.getText().toString());
            cv.put("description",this.descriptionText.getText().toString());
            cv.put("category",this.categoryText.getText().toString());
            cv.put("address_title",this.addressTitleText.getText().toString());
            cv.put("address_street",this.addressStreetText.getText().toString());
            Double elevation = Double.parseDouble(this.elevationText.getText().toString());
            cv.put("elevation",elevation);
            Double latitude = Double.parseDouble(this.latitudeText.getText().toString());
            cv.put("latitude",latitude);
            Double longitude = Double.parseDouble(this.longitudeText.getText().toString());
            cv.put("longitude",longitude);
            locDB.update("locations",cv,"NAME = ?", new String[]{name});
            locDB.close();
            db.close();
            EditActivity.this.finish();
        } catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "error editing database" + e.getMessage());
        }
    }
}
