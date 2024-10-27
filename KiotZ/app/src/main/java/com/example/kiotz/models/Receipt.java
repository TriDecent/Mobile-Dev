package com.example.kiotz.models;

import java.util.List;

public record Receipt(int ID, String DateTime, List<Product> Products,
                      double TotalPrice) implements IIdentifiable {
    @Override
    public int getID() {
        return ID;
    }
}