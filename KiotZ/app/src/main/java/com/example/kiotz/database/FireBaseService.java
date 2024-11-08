package com.example.kiotz.database;

import androidx.annotation.NonNull;

import com.example.kiotz.database.serializers.EmployeeSerializer;
import com.example.kiotz.database.serializers.ISerializer;
import com.example.kiotz.database.serializers.ProductSerializer;
import com.example.kiotz.database.serializers.ReceiptSerializer;
import com.example.kiotz.models.IIdentifiable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FireBaseService<T extends IIdentifiable> implements IDataBaseService<T> {

    private final FirebaseDatabase database;
    private final ISerializer<T> serializer;

    public FireBaseService(ISerializer<T> serializer) {
        this.database = FirebaseDatabase.getInstance();
        this.serializer = serializer;
    }

    private DatabaseReference getDatabaseReference() {
        String nodeName = getNodeNameForType(serializer.getClass());
        return database.getReference(nodeName);
    }

    private String getNodeNameForType(Class<?> type) {
        if (type.equals(EmployeeSerializer.class)) {
            return "employees";
        } else if (type.equals(ProductSerializer.class)) {
            return "products";
        } else if (type.equals(ReceiptSerializer.class)) {
            return "receipts";
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DatabaseReference ref = getDatabaseReference().push();
        serializer.serialize(ref, item);
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> removeAsync(T item) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getDatabaseReference().orderByChild("ID").equalTo(item.ID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue((databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            future.completeExceptionally(databaseError.toException());
                        } else {
                            future.complete(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<T> getByIdAsync(String id) {
        CompletableFuture<T> future = new CompletableFuture<>();
        getDatabaseReference().orderByChild("ID").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    T item = serializer.deserialize(snapshot);
                    future.complete(item);
                    return;
                }
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<T> items = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    T item = serializer.deserialize(snapshot);
                    items.add(item);
                }
                future.complete(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> updateAsync(T currentItem, T newItem) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getDatabaseReference().orderByChild("ID").equalTo(currentItem.ID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    serializer.serialize(snapshot.getRef(), newItem);
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }
}