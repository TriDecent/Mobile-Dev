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
                    .map(item -> repository.getQuantityByIdAsync(item.getID()))
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
        return repository.updateQuantityByIdAsync(item.getID(), newQuantity);
    }

    @Override
    public CompletableFuture<Void> incrementQuantityAsync(T item, int amount) {
        return repository.getQuantityByIdAsync(item.getID())
                .thenCompose(currentQuantity -> repository.updateQuantityByIdAsync(item.getID(), currentQuantity + amount));
    }

    @Override
    public CompletableFuture<Void> decrementQuantityAsync(T item, int amount) {
        return repository.getQuantityByIdAsync(item.getID())
                .thenCompose(currentQuantity -> repository.updateQuantityByIdAsync(item.getID(), Math.max(0, currentQuantity - amount)));
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        return repository.addAsync(item);
    }

    @Override
    public CompletableFuture<Void> removeByIdAsync(int id) {
        return repository.removeByIdAsync(id);
    }

    @Override
    public CompletableFuture<T> getByIdAsync(int id) {
        return repository.getByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return repository.getAllAsync();
    }

    @Override
    public CompletableFuture<Integer> getQuantityByIdAsync(int id) {
        return repository.getQuantityByIdAsync(id);
    }

    @Override
    public CompletableFuture<Void> updateQuantityByIdAsync(int id, int newQuantity) {
        return repository.updateQuantityByIdAsync(id, newQuantity);
    }
}
