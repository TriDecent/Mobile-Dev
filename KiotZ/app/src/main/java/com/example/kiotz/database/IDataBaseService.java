package com.example.kiotz.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDataBaseService<T> {
    CompletableFuture<Void> addAsync(T t);

    CompletableFuture<Void> removeByIdAsync(int id);

    CompletableFuture<T> getByIdAsync(int id);

    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<Integer> getQuantityByIdAsync(int id);

    CompletableFuture<Void> updateQuantityByIdAsync(int id, int newQuantity);
}

