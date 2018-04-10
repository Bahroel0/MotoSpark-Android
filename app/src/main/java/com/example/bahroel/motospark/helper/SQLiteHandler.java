package com.example.bahroel.motospark.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by bahroel on 22/10/17.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "motospark";

    // Login table name
    private static final String TABLE_USER = "tabel_user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "username";
    private static final String KEY_PIN = "user_pin";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ID_USER = "id_user";

    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_PIN + " TEXT," + KEY_EMAIL + " TEXT UNIQUE," + KEY_ID_USER + " TEXT UNIQUE" + ")";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        if (newVersion>oldVersion)
            // Create tables again
            onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String username, String user_pin, String email, String id_user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, username); // Nama User
        values.put(KEY_PIN, user_pin); // PIN User
        values.put(KEY_EMAIL, email); // Email User
        values.put(KEY_ID_USER, id_user); // ID User

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("username", cursor.getString(1));
            user.put("user_pin", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("id_user", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
