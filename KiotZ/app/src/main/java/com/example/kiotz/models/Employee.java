package com.example.kiotz.models;

public record Employee(int ID, String Name, String Date,
                       boolean IsAdmin) implements IIdentifiable {
    @Override
    public int ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(int id) {
        return new Employee(id, Name, Date, IsAdmin);
    }
}
