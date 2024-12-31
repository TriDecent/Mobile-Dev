package com.example.kiotz.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ProductInvoice implements Parcelable {
    private String id;
    private int quantity;
    private double totalPrice;

    public ProductInvoice(String id, int quantity, double totalPrice) {
        this.id = id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductInvoice that = (ProductInvoice) obj;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ProductInvoice(Parcel in) {
        id = in.readString();
        quantity = in.readInt();
        totalPrice = in.readDouble();
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(quantity);
        dest.writeDouble(totalPrice);

    }

    public static final Creator<ProductInvoice> CREATOR = new Creator<ProductInvoice>() {
        @Override
        public ProductInvoice createFromParcel(Parcel in) {
            return new ProductInvoice(in);
        }

        @Override
        public ProductInvoice[] newArray(int size) {
            return new ProductInvoice[size];
        }
    };
}
