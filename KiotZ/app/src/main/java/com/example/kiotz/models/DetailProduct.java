package com.example.kiotz.models;

import java.io.Serializable;

public record DetailProduct(String ID, String Name, double Price, double SellingPrice,
                            String Unit, String Category, int Total, int Sold, int Remaining,
                            double Revenue, double Profit, int Image, int barCode
                            ) implements Serializable {

    @Override
    public int Image() {
        return Image;
    }

    @Override
    public int barCode() {
        return barCode;
    }

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public double Profit() {
        return Profit;
    }

    @Override
    public double Revenue() {
        return Revenue;
    }

    @Override
    public int Remaining() {
        return Remaining;
    }

    @Override
    public int Sold() {
        return Sold;
    }

    @Override
    public int Total() {
        return Total;
    }

    @Override
    public double SellingPrice() {
        return SellingPrice;
    }

    @Override
    public String Name() {
        return Name;
    }

    @Override
    public double Price() {
        return Price;
    }

    @Override
    public String Unit() {
        return Unit;
    }

    @Override
    public String Category() {
        return Category;
    }
}
