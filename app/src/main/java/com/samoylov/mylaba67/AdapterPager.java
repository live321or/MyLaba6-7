package com.samoylov.mylaba67;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AdapterPager extends PagerAdapter {

    private ArrayList<Item> list;
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    public static Handler handler1;
    public static Handler handler;

    public AdapterPager(Context context, ArrayList<Item> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.page, container, false);
        final TextView name = view.findViewById(R.id.vp_name);
        final TextView price = view.findViewById(R.id.vp_price);
        final TextView quantity = view.findViewById(R.id.vp_quantity);
        final Button btn_buy = view.findViewById(R.id.vp_btn_buy);

        name.setText(list.get(position).getName());
        price.setText(Float.toString(list.get(position).getPrice()));
        quantity.setText(Integer.toString(list.get(position).getQuantity()));
        if (list.get(position).getQuantity() == 0) {
            btn_buy.setEnabled(false);
        } else btn_buy.setEnabled(true);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (list.size() > 0) {
                            Message msg = StoreFrontActivity.handler.obtainMessage();
                            Message msg1 = handler1.obtainMessage();
                            Message msg2 = new Message();
                            if (BackEndActivity.handler != null) {
                                msg2 = BackEndActivity.handler.obtainMessage();
                            }
                            Bundle bundle = new Bundle();
                            Bundle bundle1 = new Bundle();
                            dbHelper = new DBHelper(context);
                            database = dbHelper.getWritableDatabase();
                            if (list.get(position).getQuantity() <= 0) {
                                database.execSQL("UPDATE " + DBHelper.TABLE_PRODUCTS + " SET " +
                                        DBHelper.KEY_QUANTITY + " = 0"
                                        + " WHERE " + DBHelper.KEY_ID + " = " + list.get(position).getID() + ";");
                                list.remove(position);
                            } else {
                                database.execSQL("UPDATE " + DBHelper.TABLE_PRODUCTS + " SET " +
                                        DBHelper.KEY_QUANTITY + " = " + DBHelper.KEY_QUANTITY + " - 1"
                                        + " WHERE " + DBHelper.KEY_ID + " = " + list.get(position).getID() + ";");
                                list.get(position).setQuantity(list.get(position).getQuantity() - 1);
                                bundle.putInt("position", position);
                                bundle1.putInt("position", position);
                            }
                            database.close();
                            ArrayList<Item> arr = new ArrayList<>();
                            database = dbHelper.getWritableDatabase();
                            Cursor c = database.rawQuery(StoreFrontActivity.query, null);
                            if (c.moveToFirst()) {
                                int idColIndex = c.getColumnIndex(DBHelper.KEY_ID);
                                int nameColIndex = c.getColumnIndex(DBHelper.KEY_NAME);
                                int priceColIndex = c.getColumnIndex(DBHelper.KEY_PRICE);
                                int quantityColIndex = c.getColumnIndex(DBHelper.KEY_QUANTITY);
                                do {
                                    arr.add(new Item(c.getInt(idColIndex), c.getString(nameColIndex), c.getFloat(priceColIndex),
                                            c.getInt(quantityColIndex)));
                                } while (c.moveToNext());
                            }
                            c.close();
                            database.close();
                            bundle.putParcelableArrayList("listOfProducts", arr);
                            bundle.putParcelable("item", list.get(position));
                            bundle1.putParcelableArrayList("listOfProducts", list);
                            msg.setData(bundle);
                            msg1.setData(bundle1);
                            if (BackEndActivity.handler != null) {
                                msg2.setData(bundle1);
                                BackEndActivity.handler.sendMessage(msg2);
                            }
                            StoreFrontActivity.handler.sendMessage(msg);
                            handler1.sendMessage(msg1);
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                notifyDataSetChanged();
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean T = true;
                int pos = 0;
                System.out.println("работает");
                Item item = bundle.getParcelable("item");
                for (Item i : list) {
                    if (i.getID() == item.getID()) {
                        pos = list.indexOf(i);
                        list.set(pos, item);
                        T = false;
                        break;
                    }
                }
                if (T) {
                    list.add(item);
                    pos = list.size() - 1;
                }
                if (list.get(pos).getQuantity() <= 0) {
                    Button btn_buy = view.findViewById(R.id.vp_btn_buy);
                    btn_buy.setEnabled(false);
                }
                TextView n = view.findViewById(R.id.vp_name);
                n.setText(list.get(pos).getName());
                TextView p = view.findViewById(R.id.vp_price);
                p.setText(String.valueOf(list.get(pos).getPrice()));
                TextView q = view.findViewById(R.id.vp_quantity);
                q.setText(String.valueOf(list.get(pos).getQuantity()));
                notifyDataSetChanged();
            }

        };

        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                list = bundle.getParcelableArrayList("listOfProducts");
                if (list.size() > 0) {
                    int p = bundle.getInt("position");
                    System.out.println("уменьшить " + p);
                    if (list.get(p).getQuantity() <= 0) {
                        Button btn_buy = view.findViewById(R.id.vp_btn_buy);
                        btn_buy.setEnabled(false);
                    }
                    TextView q = view.findViewById(R.id.vp_quantity);
                    q.setText(String.valueOf(list.get(p).getQuantity()));
                    notifyDataSetChanged();
                }

            }
        };

        container.addView(view);
        return view;

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


}
