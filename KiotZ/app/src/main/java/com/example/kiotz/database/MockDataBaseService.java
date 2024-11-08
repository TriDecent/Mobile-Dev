package com.example.kiotz.database;

import com.example.kiotz.models.IIdentifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MockDataBaseService<T extends IIdentifiable> implements IDataBaseService<T> {
    private final Map<String, T> data = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public MockDataBaseService(List<T> items) {
        for (var item : items) {
            data.put(item.ID(), item);
        }
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        return CompletableFuture.runAsync(() -> {
            String id = String.valueOf(random.nextInt());
            while (data.containsKey(id)) {
                id = String.valueOf(random.nextInt());
            }
            data.put(id, (T) item.withId(id));
        });
    }

    @Override
    public CompletableFuture<Void> removeAsync(T item) {
        return CompletableFuture.runAsync(() -> {
            if (!data.containsValue(item)) {
                throw new NoSuchElementException("Item not found");
            }
            data.remove(item.ID());
        });
    }

    @Override
    public CompletableFuture<T> getByIdAsync(String id) {
        return CompletableFuture.supplyAsync(() -> {
            String idStr = String.valueOf(id);
            if (!data.containsKey(idStr)) {
                throw new NoSuchElementException("Item not found");
            }
            return data.get(idStr);
        });
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(data.values()));
    }

    @Override
    public CompletableFuture<Void> updateAsync(T currentItem, T newItem) {
        return CompletableFuture.runAsync(() -> {
            if (!data.containsValue(currentItem)) {
                throw new NoSuchElementException("Item not found");
            }
            data.put(currentItem.ID(), newItem);
        });
    }
}