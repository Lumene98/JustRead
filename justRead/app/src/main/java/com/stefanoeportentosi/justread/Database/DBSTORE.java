package com.stefanoeportentosi.justread.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stefanoeportentosi.justread.Luoghi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefanoeportentosi on 10/05/17.
 */

public class DBSTORE extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 5;
    // Database Name
    private static final String DATABASE_NAME = "RemindMe2";
    // Contacts table name
    private static final String TABLE_NAME = "luoghi";
    // Shops Table Columns names
    private static final String ID = "ID";
    private static final String nomeluogo = "nomeluogo";
    private static final String noteluogo = "noteluogo";

    private static final String lat = "lat";
    private static final String lng = "lng";
    private static final String radius = "radius";


    public DBSTORE(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createluoghitable = "CREATE TABLE " + TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY,"
                + nomeluogo + " TEXT,"
                + noteluogo + " TEXT,"
                + lat + " TEXT,"
                + lng + " TEXT,"
                + radius + " TEXT"
                + ")";
        try {
            db.execSQL(createluoghitable);
        }
        catch (SQLException ex){
            Log.d("SQLException", ex.toString());
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
         if (newVersion > oldVersion) {
             //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + noteluogo + " TEXT");
// Creating tables again
             onCreate(db);
         }
    }
    // Adding new shop
    public void addLuogo(Luoghi luoghi) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID,luoghi.getId());
        values.put(nomeluogo, luoghi.getTitolo());
        //values.put(noteluogo,luoghi.getNote());
        values.put(lat, luoghi.getLat());
        values.put(lng, luoghi.getLng());
        values.put(radius, luoghi.getRadius());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    // Getting All Shops
    public List<Luoghi> getAllLuoghi() {
        List<Luoghi> PromemoriaList = new ArrayList<Luoghi>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //Luoghi p = new Luoghi();
                //p.setID((cursor.getInt(0)));
                //p.setTitolo((cursor.getString(1)));
               // p.setNote(cursor.getString(5));
                //p.setLng(cursor.getDouble(3));
                //p.setRadius(cursor.getFloat(4));

// Adding contact to list
                //PromemoriaList.add(p);
            } while (cursor.moveToNext());
        }
        Log.d("Query", String.valueOf(cursor));
// return contact list
        return PromemoriaList;
    }
    public List<Luoghi> getLuogo(Integer id) {
        List<Luoghi> LuoghiList = new ArrayList<Luoghi>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME +" WHERE "+ ID +" like "+id ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //Luoghi p = new Luoghi();
                //p.setID((cursor.getInt(0)));
                //p.setTitolo((cursor.getString(1)));
               // p.setLat((cursor.getDouble(2)));
                //p.setLng(cursor.getDouble(3));
                //p.setRadius(cursor.getFloat(4));
                //p.setNote(cursor.getString(5));

// Adding contact to list
                //LuoghiList.add(p);
            } while (cursor.moveToNext());
        }
        Log.d("Query", String.valueOf(cursor));
// return contact list
        return LuoghiList;
    }

    // Deleting a shop
    public void deleteLuogo(Luoghi luoghi) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?",
                new String[] { String.valueOf(luoghi.getId()) });
        db.close();
    }

}



