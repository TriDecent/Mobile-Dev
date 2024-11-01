package com.example.kiotz.models;

public record Product(int ID, String Name, String Category, double Price, String Unit,
                      String QRCodePath) implements IIdentifiable {
    @Override
    public int ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(int id) {
        return new Product(id, Name, Category, Price, Unit, QRCodePath);
    }
}
