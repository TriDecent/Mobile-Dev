package com.example.kiotz.models;

public class EmployeeTotalAmountSold
{
    private String employeeID;
    private double totalAmount;

    public EmployeeTotalAmountSold(String employeeID, double totalAmount)
    {
        this.totalAmount = totalAmount;
        this.employeeID = employeeID;
    }

    public String getEmployeeID()
    {
        return employeeID;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
