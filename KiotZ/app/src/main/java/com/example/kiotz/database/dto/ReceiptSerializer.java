package com.example.kiotz.database.dto;

import com.example.kiotz.models.Receipt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ReceiptSerializer implements ISerializer<Receipt> {
    @Override
    public void serialize(DatabaseReference ref, Receipt item) {

    }

    @Override
    public Receipt deserialize(DataSnapshot snapshot) {
        return null;
    }
}
