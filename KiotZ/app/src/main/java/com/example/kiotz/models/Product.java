package com.example.kiotz.models;

public record Product(String ID, String Name, String Category, double Price, String Unit,
                      String QRCodePath) implements IIdentifiable {
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Product(id, Name, Category, Price, Unit, QRCodePath);
    }
}
