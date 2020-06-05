package com.samoylov.mylaba67;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BackEndActivity extends AppCompatActivity implements AdapterRecyclerView.OnNoteListener {
    private ArrayList<Item> list;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private ListView listProducts;
    private SimpleAdapter simpleAdapter;
    private TextView textView;

    private ArrayList<Map<String, Object>> data;
    private Map<String, Object> m;

    final String ATTRIBUTE_ID = "id";
    final String ATTRIBUTE_NAME = "image";
    final String LOG_TAG = "myLogs";
    static final int CM_DELETE_ID = 1;
    static final int CM_CHANGE_ID = 2;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
        textView = findViewById(R.id.text_view_products_not);
        listProducts = (ListView) findViewById(R.id.productsList);
        list = new ArrayList<>();
        dbHelper = new DBHelper(BackEndActivity.this);

        database = dbHelper.getWritableDatabase();
        Cursor c = database.query(DBHelper.TABLE_PRODUCTS, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);
            int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
            int priceColIndex = c.getColumnIndex(DBHelper.KEY_PRICE);
            int quantityColIndex = c.getColumnIndex(DBHelper.KEY_QUANTITY);
            do {
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", price = " + c.getFloat(priceColIndex) +
                                ", quantity = " + c.getInt(quantityColIndex));
                list.add(new Item(c.getInt(idColIndex), c.getString(nameColIndex), c.getFloat(priceColIndex),
                        c.getInt(quantityColIndex)));
            } while (c.moveToNext());
            data = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_ID, list.get(i).getID());
                m.put(ATTRIBUTE_NAME, list.get(i).getName());
                data.add(m);
            }

            int[] to = { R.id.text_view_id, R.id.text_view_name };
            String[] from = { ATTRIBUTE_ID, ATTRIBUTE_NAME };

            simpleAdapter = new SimpleAdapter(this, data, R.layout.item_back, from, to);
            listProducts.setAdapter(simpleAdapter);
            textView.setVisibility(View.GONE);
            listProducts.setVisibility(View.VISIBLE);
            registerForContextMenu(listProducts);
        } else {
            listProducts.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Log.d(LOG_TAG, "0 rows");
        }
        c.close();
        database.close();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                list = bundle.getParcelableArrayList("listOfProducts");
                if (list.size() > 0) {
                    int p = bundle.getInt("position");
                    Toast toast = Toast.makeText(BackEndActivity.this, "Товар \"" + list.get(p).getName()
                            + "\" изменен", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                            0,
                            200);
                    toast.show();
                    textView.setVisibility(View.GONE);
                    listProducts.setVisibility(View.VISIBLE);
                }
                if (list.size() == 0) {
                    Toast toast = Toast.makeText(BackEndActivity.this, "Товаров нет", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP,
                            0,
                            200);
                    toast.show();
                    listProducts.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        };
    }
    public void add(View v) {
        Intent i = new Intent(BackEndActivity.this, BackEndChangeActivity.class);
        i.putExtra("change_id", 0);
        startActivity(i);
        finish();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, CM_CHANGE_ID, Menu.NONE, "Изменить запись");
        menu.add(Menu.NONE, CM_DELETE_ID, Menu.NONE, "Удалить запись");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "clicked");
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            database = dbHelper.getWritableDatabase();
            database.execSQL("DELETE FROM " + DBHelper.TABLE_PRODUCTS + " WHERE " + DBHelper.KEY_ID
                    + " = " + list.get(acmi.position).getID() + ";");
            database.close();
            Toast toast = Toast.makeText(BackEndActivity.this, "Товар  с id " +
                    list.get(acmi.position).getID() + " удален", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP,
                    0,
                    200);
            toast.show();
            data.remove(acmi.position);
            list.remove(acmi.position);
            simpleAdapter.notifyDataSetChanged();
            return true;
        }
        if (item.getItemId() == CM_CHANGE_ID) {
            Intent i = new Intent(BackEndActivity.this, BackEndChangeActivity.class);
            i.putExtra("change_id", 1);
            i.putExtra ("item_front", list.get(acmi.position));
            i.putParcelableArrayListExtra("listOfProducts", list);
            i.putExtra("position", acmi.position);
            startActivity(i);
            finish();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onNoteClick(int position) {
        Log.d(LOG_TAG, "clicked");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_back:
                Intent i = new Intent(BackEndActivity.this, StoreFrontActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
