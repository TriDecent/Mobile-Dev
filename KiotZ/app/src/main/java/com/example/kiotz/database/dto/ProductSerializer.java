package com.example.kiotz.database.dto;

import com.example.kiotz.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ProductSerializer implements ISerializer<Product> {

    @Override
    public void serialize(DatabaseReference ref, Product product) {
        ref.child("ID").setValue(product.ID());
        ref.child("Name").setValue(product.Name());
        ref.child("Category").setValue(product.Category());
        ref.child("Price").setValue(product.Price());
        ref.child("Unit").setValue(product.Unit());
        ref.child("Quantity").setValue(product.Quantity());
        ref.child("imageURL").setValue(product.imageURL());
    }

    @Override
    public Product deserialize(DataSnapshot snapshot) {
        String id = snapshot.child("ID").getValue(String.class);
        String name = snapshot.child("Name").getValue(String.class);
        String category = snapshot.child("Category").getValue(String.class);
        Double price = snapshot.child("Price").getValue(Double.class);
        String unit = snapshot.child("Unit").getValue(String.class);
        Integer quantity = snapshot.child("Quantity").getValue(Integer.class);
        String imageURL = snapshot.child("imageURL").getValue(String.class);

        return new Product(
                id != null ? id : "",
                name != null ? name : "",
                category != null ? category : "",
                price != null ? price : 0.0,
                unit != null ? unit : "",
                quantity != null ? quantity : 0,
                imageURL != null ? imageURL : ""
        );
    }
}
