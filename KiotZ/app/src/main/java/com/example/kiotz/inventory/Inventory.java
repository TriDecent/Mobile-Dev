package com.example.kiotz.inventory;

import com.example.kiotz.models.IIdentifiable;
import com.example.kiotz.repositories.IRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Inventory<T extends IIdentifiable> implements IInventory<T> {
    private final IRepository<T> repository;

    public Inventory(IRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<Integer> getTotalItemsAsync() {
        return repository.getAllAsync().thenApply(List::size);
    }

    @Override
    public CompletableFuture<Integer> getTotalQuantityAsync() {
        return repository.getAllAsync().thenCompose(items -> {
            List<CompletableFuture<Integer>> quantities = items.stream()
                    .map(item -> repository.getQuantityAsync(item.getID()))
                    .collect(Collectors.toList());

            // Wait for all quantities to be fetched and then sum them up
            return CompletableFuture.allOf(quantities.toArray(new CompletableFuture[0]))
                    .thenApply(v -> quantities.stream()
                            .mapToInt(CompletableFuture::join) // Using join to get the result of the CompletableFuture
                            .sum());
        });
    }

    @Override
    public CompletableFuture<Void> updateItemQuantityAsync(T item, int newQuantity) {
        return repository.updateQuantityAsync(item.getID(), newQuantity);
    }

    @Override
    public CompletableFuture<Void> incrementQuantityAsync(T item, int amount) {
        return repository.getQuantityAsync(item.getID())
                .thenCompose(currentQuantity -> repository.updateQuantityAsync(item.getID(), currentQuantity + amount));
    }

    @Override
    public CompletableFuture<Void> decrementQuantityAsync(T item, int amount) {
        return repository.getQuantityAsync(item.getID())
                .thenCompose(currentQuantity -> repository.updateQuantityAsync(item.getID(), Math.max(0, currentQuantity - amount)));
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        return repository.addAsync(item);
    }

    @Override
    public CompletableFuture<Void> removeAsync(int id) {
        return repository.removeAsync(id);
    }

    @Override
    public CompletableFuture<T> getAsync(int id) {
        return repository.getAsync(id);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return repository.getAllAsync();
    }

    @Override
    public CompletableFuture<Integer> getQuantityAsync(int id) {
        return repository.getQuantityAsync(id);
    }

    @Override
    public CompletableFuture<Void> updateQuantityAsync(int id, int newQuantity) {
        return repository.updateQuantityAsync(id, newQuantity);
    }
}
