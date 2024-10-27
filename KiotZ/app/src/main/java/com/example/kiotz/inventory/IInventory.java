package com.example.kiotz.inventory;

import com.example.kiotz.repositories.IRepository;

import java.util.concurrent.CompletableFuture;

public interface IInventory<T> extends IRepository<T> {
    CompletableFuture<Integer> getTotalItemsAsync();

    CompletableFuture<Integer> getTotalQuantityAsync();

    CompletableFuture<Void> updateItemQuantityAsync(T item, int newQuantity);

    CompletableFuture<Void> incrementQuantityAsync(T item, int amount);

    CompletableFuture<Void> decrementQuantityAsync(T item, int amount);

}
