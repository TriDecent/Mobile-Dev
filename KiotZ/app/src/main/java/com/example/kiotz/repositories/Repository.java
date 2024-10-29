package com.example.kiotz.repositories;

import com.example.kiotz.database.IDataBaseService;
import com.example.kiotz.models.IIdentifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Repository<T extends IIdentifiable> implements IRepository<T> {
    private final IDataBaseService<T> databaseService;

    public Repository(IDataBaseService<T> databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        return databaseService.addAsync(item);
    }

    @Override
    public CompletableFuture<Void> removeByIdAsync(int id) {
        return databaseService.removeByIdAsync(id);
    }

    @Override
    public CompletableFuture<T> getByIdAsync(int id) {
        return databaseService.getByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return databaseService.getAllAsync();
    }

    @Override
    public CompletableFuture<Integer> getQuantityByIdAsync(int id) {
        return databaseService.getQuantityByIdAsync(id);
    }

    @Override
    public CompletableFuture<Void> updateQuantityByIdAsync(int id, int newQuantity) {
        return databaseService.updateQuantityByIdAsync(id, newQuantity);
    }
}
