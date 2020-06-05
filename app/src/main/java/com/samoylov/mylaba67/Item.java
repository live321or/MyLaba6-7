package com.samoylov.mylaba67;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String Name;
    private float Price;
    private int Quantity;
    private int id;

    public Item(String Name) {
        this.Name = Name;
        this.Price = 0.0f;
        this.Quantity = 0;
        this.id = 0;
    }

    public Item(String Name, float Price, int Quantity) {
        this(Name);
        this.Price = Price;
        this.Quantity = Quantity;
    }

    public Item(int id, String Name, float Price, int Quantity) {
        this(Name, Price, Quantity);
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public float getPrice() {
        return Price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPrice(float price) {
        Price = price;
    }

    protected Item(Parcel in) {
        this.id = in.readInt();
        this.Name = in.readString();
        this.Price = in.readFloat();
        this.Quantity = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.Name);
        dest.writeFloat(this.Price);
        dest.writeInt(this.Quantity);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
