package com.samoylov.mylaba67;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ProductDB";
    public static final String TABLE_PRODUCTS = "Products";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_PRICE = "Price";
    public static final String KEY_QUANTITY = "Quantity";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PRODUCTS + "(" + KEY_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_NAME + " TEXT(40), " + KEY_PRICE + " REAL DEFAULT 0.0," + KEY_QUANTITY +
                " INTEGER(5) DEFAULT 0);");
        System.out.println("ТБ создана");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
