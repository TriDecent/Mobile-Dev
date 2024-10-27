package com.example.kiotz.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDataBaseService<T> {
    CompletableFuture<Void> addAsync(T t);

    CompletableFuture<Void> removeAsync(int id);

    CompletableFuture<T> getAsync(int id);

    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<Integer> getQuantityAsync(int id);

    CompletableFuture<Void> updateQuantityAsync(int id, int newQuantity);
}

