package com.example.kiotz.models;

public record Employee(String ID, String Email, String Name, String Date,
                       boolean IsAdmin) implements IIdentifiable {
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Employee(id, Email, Name, Date, IsAdmin);
    }
}
