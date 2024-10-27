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
    public CompletableFuture<Void> removeAsync(int id) {
        return databaseService.removeAsync(id);
    }

    @Override
    public CompletableFuture<T> getAsync(int id) {
        return databaseService.getAsync(id);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return databaseService.getAllAsync();
    }

    @Override
    public CompletableFuture<Integer> getQuantityAsync(int id) {
        return databaseService.getQuantityAsync(id);
    }

    @Override
    public CompletableFuture<Void> updateQuantityAsync(int id, int newQuantity) {
        return databaseService.updateQuantityAsync(id, newQuantity);
    }
}
