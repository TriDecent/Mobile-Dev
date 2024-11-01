package com.example.kiotz.models;

import java.util.List;

public record Receipt(int ID, String DateTime, List<Product> Products,
                      double TotalPrice) implements IIdentifiable {
    @Override
    public int ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(int id) {
        return new Receipt(id, DateTime, Products, TotalPrice);
    }
}