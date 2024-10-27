package com.example.kiotz.models;

public record Employee(int ID, String Name, String Date,
                       boolean IsAdmin) implements IIdentifiable, IEmployee {
    @Override
    public int getID() {
        return ID;
    }
}
