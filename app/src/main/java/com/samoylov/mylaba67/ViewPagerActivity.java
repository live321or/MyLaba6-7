package com.samoylov.mylaba67;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ArrayList<Item> r;
    private int positionItem;

    static final String TAG = "myLogs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        Intent i = getIntent();
        r = i.getParcelableArrayListExtra("listOfItems");
        positionItem = i.getIntExtra("position", 0);

        viewPager = findViewById(R.id.Vpager);
        PagerAdapter adapter = new AdapterPager(this, r);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(positionItem);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(ViewPagerActivity.this, StoreFrontActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ViewPagerActivity.this, StoreFrontActivity.class);
        startActivity(i);
        finish();
    }
}
