package com.example.kiotz.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDataBaseService<T> {
    CompletableFuture<Void> addAsync(T item);

    CompletableFuture<Void> removeAsync(T item);

    CompletableFuture<T> getByIdAsync(String id);

    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<Void> updateAsync(T currentItem, T newItem);
}

