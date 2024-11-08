package com.example.kiotz.database.serializers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public interface ISerializer<T> {
    void serialize(DatabaseReference ref, T item);

    T deserialize(DataSnapshot snapshot);
}