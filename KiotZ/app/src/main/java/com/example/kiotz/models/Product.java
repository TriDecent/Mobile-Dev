package com.example.kiotz.models;

public record Product(int ID, String Name, String Category, double Price, String Unit,
                      String QRCodePath) implements IIdentifiable {
    @Override
    public int getID() {
        return ID;
    }
}
