package com.example.kiotz.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public record Product(String ID, String Name, String Category, double Price, String Unit, Integer Quantity
                      , String imageURL) implements IIdentifiable, Parcelable {
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Product(id, Name, Category, Price, Unit, Quantity,imageURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(Name);
        dest.writeString(Category);
        dest.writeDouble(Price);
        dest.writeString(Unit);
        dest.writeValue(Quantity);
        dest.writeString(imageURL);
    }

     public Product(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                in.readString(),
                in.readDouble(),
                in.readString(),
                (Integer) in.readValue(Integer.class.getClassLoader()),
                in.readString()
        );
    }


    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
