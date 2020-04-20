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
 * Purpose: To calculate the great circle distance and initial bearing
 *
 * @author Tim Jordan   mailto:tsjorda1@asu.edu
 * @version November 2019
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class CalculationsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private PlaceDescription placeStart, result;
    private PlaceDescription placeEnd;
    private static final Double DEG_TO_RAD = Math.PI/180.0;
    private Double lat1, phi1;
    private Double lat2, phi2;
    private Double long1, lam1;
    private Double long2, lam2;
    private Button btn_calc;
    private Spinner spn_place1;
    private Spinner spn_place2;
    private TextView tv_greatCircle;
    private TextView tv_initialHeading;
    private LocationDB db;
    private String[] locs;
    private ArrayAdapter<String> locAdapter;
    private AdapterView.OnItemSelectedListener listener_spn1;
    private AdapterView.OnItemSelectedListener listener_spn2;
    private String selectedPlace1, selectedPlace2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculations);

        spn_place1 = findViewById(R.id.spn_place1);
        spn_place2 = findViewById(R.id.spn_place2);
        btn_calc = findViewById(R.id.btn_doCalc);
        tv_greatCircle = findViewById(R.id.result_GreatCircle);
        tv_initialHeading = findViewById(R.id.result_initBearing);

        loadSpinners();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spn_place1) {
            this.selectedPlace1 = spn_place1.getSelectedItem().toString();
        }

        if(parent.getId() == R.id.spn_place2) {
            this.selectedPlace2 = spn_place2.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        android.util.Log.d(this.getClass().getSimpleName(), "onNothingSelected");
    }

    private void loadSpinners() {
        try {
            db = new LocationDB(this);
            SQLiteDatabase locDB = db.openDB();
            Cursor cur = locDB.rawQuery("SELECT name FROM locations;", new String[]{});
            ArrayList<String> al = new ArrayList<>();
            while(cur.moveToNext()) {
                try {
                    al.add(cur.getString(0));
                }catch (Exception e) {
                    android.util.Log.w(this.getClass().getSimpleName(), "exception stepping through cursor "+e.getMessage());
                }
            }
            locs = al.toArray(new String[al.size()]);
            locAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,locs);
            spn_place1.setAdapter(locAdapter);
            spn_place2.setAdapter(locAdapter);
            spn_place1.setOnItemSelectedListener(this);
            spn_place2.setOnItemSelectedListener(this);
        }catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "unable to setup location list view");
        }
    }

    private double calculateGreatCircle(double lat1, double lat2, double long1, double long2) {
        double degToRad = Math.PI/180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return 6371.01 * Math.acos(Math.sin(phi1)*Math.sin(phi2)+Math.cos(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1));
    }

    private double calculateInitialBearing(double lat1, double lat2, double long1, double long2) {
        double degToRad = Math.PI/180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        double bearing = Math.atan2(Math.sin(lam2 - lam1)*Math.cos(phi2),
                Math.cos(phi1)*Math.sin(phi2)-Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1))*180/Math.PI;

        double initialBearing = (bearing + 360)%360;

        return initialBearing;
    }

    public void doCalculations(View v) {
        placeStart = getSelectedDescription(selectedPlace1);
        placeEnd = getSelectedDescription(selectedPlace2);
        lat1 = placeStart.latitude;
        lat2 = placeEnd.latitude;
        long1 = placeStart.longitude;
        long2 = placeEnd.longitude;
        double greatCircle = calculateGreatCircle(lat1, lat2, long1, long2);
        double initBearing = calculateInitialBearing(lat1, lat2, long1, long2);
        String greatCircleStr = String.format("%.9g%n", greatCircle);
        String initBearingStr = String.format("%.7g%n", initBearing);
        tv_greatCircle.setText(greatCircleStr);
        tv_initialHeading.setText(initBearingStr);
    }

    public PlaceDescription getSelectedDescription (String placeName) {
        try {
            LocationDB db = new LocationDB(this);
            SQLiteDatabase locDB = db.openDB();
            Cursor cur = locDB.rawQuery("SELECT * FROM locations where name=? ;",
                    new String[]{placeName});
            String description = "unknown";
            String category = "unknown";
            String addressT = "unknown";
            String addressS = "unknown";
            Double elevation = 0.0;
            Double latitude = 0.0;
            Double longitude = 0.0;
            while (cur.moveToNext()) {
                description = cur.getString(1);
                category = cur.getString(2);
                addressT = cur.getString(3);
                addressS = cur.getString(4);
                elevation = cur.getDouble(5);
                latitude = cur.getDouble(6);
                longitude = cur.getDouble(7);
            }
            result = new PlaceDescription(placeName,description,category,addressT,addressS,elevation,latitude,longitude);
            cur.close();
            locDB.close();
            db.close();
        } catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(), "Exception getting location info: " + e.getMessage());
        }
        return result;
    }
}
