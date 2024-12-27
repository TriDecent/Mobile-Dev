package com.example.kiotz.models;

import java.time.LocalDateTime;
import java.util.List;

public record Receipt(String ID, LocalDateTime DateTime, String EmployeeId, String CustomerName,
                      String CustomerPhone,
                      List<String> ProductIds,
                      double TotalPrice) implements IIdentifiable {
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Receipt(id, DateTime, CustomerName, EmployeeId, CustomerPhone, ProductIds, TotalPrice);
    }


}