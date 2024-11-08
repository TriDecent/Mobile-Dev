package com.example.kiotz.models;

public record Account(String UserName, String Password,
                      String EmployeeID) implements IIdentifiable {
    @Override
    public String ID() {
        return EmployeeID;
    }

    @Override
    public IIdentifiable withId(String id) {
        return new Account(UserName, Password, id);
    }
}
