package com.example.kiotz.models;

public record CustomProduct(String ID, String Name, String Category,double Price, double sellingPrice, String Unit,
                            String QRCodePath,int Image){
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public String Name() {
        return Name;
    }

    @Override
    public String Category() {
        return Category;
    }

    @Override
    public double Price() {
        return Price;
    }

    @Override
    public double sellingPrice() {
        return sellingPrice;
    }

    @Override
    public String Unit() {
        return Unit;
    }

    @Override
    public int Image() {
        return Image;
    }

    @Override
    public String QRCodePath() {
        return QRCodePath;
    }
}
