package com.example.kiotz.database.serializers;

import com.example.kiotz.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ProductSerializer implements ISerializer<Product> {

    @Override
    public void serialize(DatabaseReference ref, Product product) {

    }

    @Override
    public Product deserialize(DataSnapshot snapshot) {

        return null;
    }
}
