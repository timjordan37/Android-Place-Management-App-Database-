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
 * Purpose: To initialize the database and seed locations
 *
 * @author Tim Jordan   mailto:tsjorda1@asu.edu
 * @version November 2019
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class LocationDB extends SQLiteOpenHelper {

    private static boolean debugon = false;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationsdb";
    private String dbPath;
    private SQLiteDatabase locDB;
    private final Context context;

    public LocationDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        dbPath = context.getFilesDir().getPath()+"/";
        android.util.Log.d(this.getClass().getSimpleName(), "dbPath: "+dbPath);
    }

    public void createDB() throws IOException {
        this.getReadableDatabase();
        try {
            copyDB();
        } catch (IOException e) {
            android.util.Log.w(this.getClass().getSimpleName(), "createDB error copying database" + e.getMessage());
        }
    }

    private boolean checkDB() {
        SQLiteDatabase checkDB = null;
        boolean ret = false;
        try {
            String path = dbPath + DATABASE_NAME + ".db";
            //debug("CourseDB --> checkdb: path to db is: ", path);
            File aFile = new File(path);
            if(aFile.exists()){
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
                if (checkDB != null) {
                    Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name = 'locations';", null);
                    boolean locTabExists = false;
                    if(tabChk == null){
                        debug("locDB --> checkDB","check for course table result set is null");
                    } else {
                        tabChk.moveToNext();
                        locTabExists = !tabChk.isAfterLast();
                    }
                    if(locTabExists){
                        Cursor c = checkDB.rawQuery("SELECT * FROM locations", null);
                        c.moveToFirst();
                        while(!c.isAfterLast()) {
                            String locName = c.getString(0);
                            String locDesc = c.getString(1);
                            String locCat = c.getString(2);
                            String locAddT = c.getString(3);
                            String locAddS = c.getString(4);
                            Double locElev = c.getDouble(5);
                            Double locLat = c.getDouble(6);
                            Double locLong = c.getDouble(7);
                            c.moveToNext();
                        }
                        ret = true;
                    }
                }
            }
        } catch (SQLiteException e) {
            android.util.Log.w("checkDB",e.getMessage());
        }
        if(checkDB != null) {
            checkDB.close();
        }
        return ret;
    }

    public void copyDB() throws IOException {
        try {
            if(!checkDB()) {
                InputStream ip = context.getResources().openRawResource(R.raw.locationsdb);
                File aFile = new File(dbPath);
                if (!aFile.exists()) {
                    aFile.mkdirs();
                }
                String op = dbPath + DATABASE_NAME + ".db";
                OutputStream output = new FileOutputStream(op);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = ip.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                ip.close();
            }
        } catch (IOException e) {
            android.util.Log.w("copyDB","IOException " + e.getMessage());
        }
    }

    public SQLiteDatabase openDB() throws SQLException {
        String mypath = dbPath + DATABASE_NAME + ".db";
        if(checkDB()) {
            locDB = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
        } else {
            try {
                this.copyDB();
                locDB = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
            } catch (Exception ex) {
                android.util.Log.w(this.getClass().getSimpleName(), "unable to copy and open db "+ex.getMessage());
            }
        }
        return locDB;
    }

    @Override
    public synchronized void close() {
        if(locDB !=null)
            locDB.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void debug(String hdr, String msg) {
        if(debugon) {
            android.util.Log.d(hdr,msg);
        }
    }
}
