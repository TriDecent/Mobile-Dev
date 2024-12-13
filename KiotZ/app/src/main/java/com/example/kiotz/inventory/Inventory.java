package com.example.kiotz.inventory;

import android.media.Image;
import android.net.Uri;

import com.example.kiotz.models.IIdentifiable;
import com.example.kiotz.repositories.IRepository;
import com.example.kiotz.utilities.ImageUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Inventory<T extends IIdentifiable> implements IInventory<T> {
    private final IRepository<T> repository;

    public Inventory(IRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<Void> addAsync(T item) {
        return repository.addAsync(item);
    }

    @Override
    public CompletableFuture<T> getByIdAsync(String id) {
        return repository.getByIdAsync(id);
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return repository.getAllAsync();
    }

    @Override
    public CompletableFuture<Void> updateAsync(T currentItem, T newItem) {
        return repository.updateAsync(currentItem, newItem);
    }

    @Override
    public CompletableFuture<Void> removeAsync(T item) {
        return repository.removeAsync(item);
    }

    @Override
    public CompletableFuture<String> uploadImageAsync(Uri imageUri, String imageName) {
        return repository.uploadImageAsync(imageUri, imageName);
    }

    @Override
    public CompletableFuture<Image> getImageAsync(String imageUri) {
        return repository.getBitmapAsync(imageUri)
                .thenApply(bitmap -> ImageUtils.convertBitmapToImage(bitmap, imageUri));
    }
}