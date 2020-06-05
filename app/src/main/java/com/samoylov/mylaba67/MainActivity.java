package com.samoylov.mylaba67;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Item> list;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_ymain);
        list = new ArrayList<>();
        dbHelper = new DBHelper(MainActivity.this);
        if (savedInstanceState == null)
            new PrefetchData().execute();
    }

    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            database = dbHelper.getWritableDatabase();
            String query = "SELECT * FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.KEY_QUANTITY + "<>0;";
            Cursor c = database.rawQuery(query, null);
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);
                int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
                int priceColIndex = c.getColumnIndex(DBHelper.KEY_PRICE);
                int quantityColIndex = c.getColumnIndex(DBHelper.KEY_QUANTITY);
                do {
                    list.add(new Item(c.getInt(idColIndex), c.getString(nameColIndex), c.getFloat(priceColIndex),
                            c.getInt(quantityColIndex)));
                } while (c.moveToNext());
            }
            c.close();
            database.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent i = new Intent(MainActivity.this, StoreFrontActivity.class);
            i.putParcelableArrayListExtra("listOfItems", list);
            startActivity(i);
            finish();
        }
    }


}
