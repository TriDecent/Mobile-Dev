package com.example.kiotz.database.dto;

import com.example.kiotz.models.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class EmployeeSerializer implements ISerializer<Employee> {

    @Override
    public void serialize(DatabaseReference ref, Employee employee) {
        ref.child("ID").setValue(employee.ID());
        ref.child("Email").setValue(employee.Email());
        ref.child("Name").setValue(employee.Name());
        ref.child("Date").setValue(employee.Date());
        ref.child("IsAdmin").setValue(employee.IsAdmin());
    }

    @Override
    public Employee deserialize(DataSnapshot snapshot) {
        String id = snapshot.child("ID").getValue(String.class);
        String email = snapshot.child("Email").getValue(String.class);
        String name = snapshot.child("Name").getValue(String.class);
        String date = snapshot.child("Date").getValue(String.class);
        boolean isAdmin = Boolean.TRUE.equals(snapshot.child("IsAdmin").getValue(Boolean.class));
        return new Employee(id, email, name, date, isAdmin);
    }
}
