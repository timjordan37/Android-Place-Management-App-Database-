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
 * Purpose: To display a place description
 *
 * @author Tim Jordan   mailto:tsjorda1@asu.edu
 * @version November 2019
 */

import org.json.JSONObject;

public class PlaceDescription {

    protected String name;
    protected String description;
    protected String category;
    protected String address_title;
    protected String address_street;
    protected Double elevation;
    protected Double latitude;
    protected Double longitude;

    PlaceDescription (String _name, String _description, String _category, String _address_title, String _address_street,
                      Double _elevation, Double _latitude, Double _longitude) {

        name = _name;
        description = _description;
        category = _category;
        address_title = _address_title;
        address_street = _address_street;
        elevation = _elevation;
        latitude = _latitude;
        longitude = _longitude;

    }

    PlaceDescription (String jsonStr) {

        try {
            JSONObject jo = new JSONObject(jsonStr);
            name = jo.getString("name");
            description = jo.getString("description");
            category = jo.getString("category");
            address_title = jo.getString("address_title");
            address_street = jo.getString("address_street");
            elevation = jo.getDouble("elevation");
            latitude = jo.getDouble("latitude");
            longitude = jo.getDouble("longitude");
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(),
                    "errror converting to/from json");
        }
    }

    public String toJsonString () {
        String ret = "";
        try {
            JSONObject jo = new JSONObject();
            jo.put("name",name);
            jo.put("description", description);
            jo.put("category", category);
            jo.put("address_title", address_title);
            jo.put("address_street", address_street);
            jo.put("elevation", elevation);
            jo.put("latitude", latitude);
            jo.put("longitude", longitude);
        }catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "errror converting to/from json");
        }
        return ret;
    }
}
