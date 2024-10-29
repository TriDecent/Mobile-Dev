package com.example.kiotz.database;

import com.example.kiotz.models.IIdentifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

// Checking will be implemented in the database service

public class MockDataBaseService<T extends IIdentifiable> implements IDataBaseService<T> {
    private final Map<Integer, T> database = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> quantities = new ConcurrentHashMap<>();

    public MockDataBaseService(List<T> items) {
        Random random = new Random();
        for (T item : items) {
            if (database.containsKey(item.getID())) {
                throw new IllegalArgumentException("Item already exists.");
            }
            database.put(item.getID(), item);
            quantities.put(item.getID(), random.nextInt(100));
        }
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        if (database.containsKey(item.getID())) {
            throw new IllegalArgumentException("Item already exists.");
        }
        database.put(item.getID(), item);
        quantities.put(item.getID(), 0);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> removeByIdAsync(int id) {
        if (!database.containsKey(id)) {
            throw new NoSuchElementException("Item not found.");
        }
        database.remove(id);
        quantities.remove(id);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<T> getByIdAsync(int id) {
        T item = database.get(id);
        if (item == null) {
            throw new NoSuchElementException("Item not found.");
        }
        return CompletableFuture.completedFuture(item);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.completedFuture(new ArrayList<>(database.values()));
    }

    @Override
    public CompletableFuture<Integer> getQuantityByIdAsync(int id) {
        Integer quantity = quantities.get(id);
        if (quantity == null) {
            throw new NoSuchElementException("Item not found.");
        }
        return CompletableFuture.completedFuture(quantity);
    }

    @Override
    public CompletableFuture<Void> updateQuantityByIdAsync(int id, int newQuantity) {
        if (!database.containsKey(id)) {
            throw new NoSuchElementException("Item not found.");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        quantities.put(id, newQuantity);
        return CompletableFuture.completedFuture(null);
    }
}
