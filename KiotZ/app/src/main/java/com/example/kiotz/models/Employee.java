package com.example.kiotz.models;

import com.example.kiotz.enums.Gender;

import java.io.Serializable;

public record Employee(String ID, String Email, String Name, String Date, Gender Gender,
                       boolean IsAdmin) implements IIdentifiable, Serializable {
    @Override
    public String ID() {
        return ID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Employee(id, Email, Name, Date, Gender, IsAdmin);
    }
}