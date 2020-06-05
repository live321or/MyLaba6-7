package com.samoylov.mylaba67;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoreFrontActivity extends AppCompatActivity implements AdapterRecyclerView.OnNoteListener {
    private RecyclerView recyclerView;
    private AdapterRecyclerView adapterRecyclerView;
    private ArrayList<Item> list;
    private final String TAG = "gg";
    private TextView textView;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    public static Handler handler;
    public static Handler handler1;
    public static String query = "SELECT * FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.KEY_QUANTITY + "<>0;";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        recyclerView = findViewById(R.id.recycle_view);
        textView = findViewById(R.id.text_view_products_not);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        Intent i = getIntent();
        list = new ArrayList<>();
        dbHelper = new DBHelper(StoreFrontActivity.this);
        if (i.hasExtra("listOfItems")) {
            list = i.getParcelableArrayListExtra("listOfItems");
        } else {
            database = dbHelper.getWritableDatabase();
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
        }
        addList();

        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                list = bundle.getParcelableArrayList("listOfProducts");
                Item it =  bundle.getParcelable("item");
                Toast toast1 = Toast.makeText(StoreFrontActivity.this, "Товар \"" + it.getName()
                        + "\" изменен", Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                        0,
                        200);
                toast1.show();
                recyclerView.setAdapter(new AdapterRecyclerView(StoreFrontActivity.this, list, StoreFrontActivity.this));
                recyclerView.getAdapter().notifyDataSetChanged();
                if (list.size() > 0) {
                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (list.size() == 0) {
                    Toast toast = Toast.makeText(StoreFrontActivity.this, "Товаров нет", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                            0,
                            200);
                    toast.show();
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        };


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                list = bundle.getParcelableArrayList("listOfProducts");
                Item it =  bundle.getParcelable("item");
                Toast toast1 = Toast.makeText(StoreFrontActivity.this, "Товар \"" + it.getName()
                        + "\" куплен", Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                        0,
                        200);
                toast1.show();
                recyclerView.setAdapter(new AdapterRecyclerView(StoreFrontActivity.this, list, StoreFrontActivity.this));
                recyclerView.getAdapter().notifyDataSetChanged();
                if (list.size() > 0) {
                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (list.size() == 0) {
                    Toast toast = Toast.makeText(StoreFrontActivity.this, "Товаров нет", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                            0,
                            200);
                    toast.show();
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        };
    }


    private void addList() {
        if (list.size() > 0) {
            adapterRecyclerView = new AdapterRecyclerView(StoreFrontActivity.this, list, this);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapterRecyclerView);
        } else {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "clicked");
        Intent i = new Intent(StoreFrontActivity.this, ViewPagerActivity.class);
        i.putParcelableArrayListExtra("listOfItems", list);
        i.putExtra("position", position);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_store:
                Intent i = new Intent(StoreFrontActivity.this, BackEndActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
