package com.example.kiotz.models;

public record Account(String UserName, String Password, int EmployeeID) implements IIdentifiable {
    @Override
    public int ID() {
        return EmployeeID;
    }

    @Override
    public IIdentifiable withId(int id) {
        return new Account(UserName, Password, id);
    }
}
