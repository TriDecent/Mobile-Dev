package com.example.kiotz.repositories;

import android.graphics.Bitmap;
import android.net.Uri;

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
    public CompletableFuture<Void> removeAsync(T item) {
        return databaseService.removeAsync(item);
    }

    @Override
    public CompletableFuture<Void> updateAsync(T currentItem, T newItem) {
        return databaseService.updateAsync(currentItem, newItem);
    }

    @Override
    public CompletableFuture<T> getByIdAsync(String id) {
        return databaseService.getByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return databaseService.getAllAsync();
    }

    @Override
    public CompletableFuture<String> uploadImageAsync(Uri imageUri, String imageName) {
        return databaseService.uploadImageAsync(imageUri, imageName);
    }

    @Override
    public CompletableFuture<Bitmap> getBitmapAsync(String imageUri) {
        return databaseService.getBitmapAsync(imageUri);
    }
}
