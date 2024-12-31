package com.example.kiotz.database.dto;

import com.example.kiotz.models.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReceiptSerializer implements ISerializer<Receipt> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void serialize(DatabaseReference ref, Receipt receipt) {
        ref.child("ID").setValue(ref.push().getKey());
        ref.child("DateTime").setValue(receipt.DateTime().format(formatter));
        ref.child("EmployeeId").setValue(receipt.EmployeeId());
        ref.child("CustomerName").setValue(receipt.CustomerName());
        ref.child("CustomerPhone").setValue(receipt.CustomerPhone());
        ref.child("ProductIds").setValue(receipt.ProductIds());
        ref.child("TotalPrice").setValue(receipt.TotalPrice());
    }

    @Override
    public Receipt deserialize(DataSnapshot snapshot) {
        String id = snapshot.child("ID").getValue(String.class);
        String dateTimeStr = snapshot.child("DateTime").getValue(String.class);
        String employeeId = snapshot.child("EmployeeId").getValue(String.class);
        String customerName = snapshot.child("CustomerName").getValue(String.class);
        String customerPhone = snapshot.child("CustomerPhone").getValue(String.class);
        Double totalPrice = snapshot.child("TotalPrice").getValue(Double.class);

        List<String> productIds = new ArrayList<>();
        DataSnapshot productIdsSnapshot = snapshot.child("ProductIds");
        for (DataSnapshot productIdSnapshot : productIdsSnapshot.getChildren()) {
            String productId = productIdSnapshot.getValue(String.class);
            if (productId != null) {
                productIds.add(productId);
            }
        }

        var dateTime = LocalDateTime.parse(dateTimeStr, formatter);

        return new Receipt(
                id,
                dateTime,
                employeeId,
                customerName,
                customerPhone,
                productIds,
                totalPrice != null ? totalPrice : 0.0
        );

    }


}