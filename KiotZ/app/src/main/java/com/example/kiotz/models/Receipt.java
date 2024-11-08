package com.example.kiotz.models;

import java.util.List;

public record Receipt(String ID, String DateTime, List<Product> Products,
                      double TotalPrice) implements IIdentifiable {
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Receipt(id, DateTime, Products, TotalPrice);
    }
}