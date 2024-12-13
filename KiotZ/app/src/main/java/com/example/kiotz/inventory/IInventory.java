package com.example.kiotz.inventory;

import android.media.Image;
import android.net.Uri;

import com.example.kiotz.models.IIdentifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IInventory<T extends IIdentifiable> {
    CompletableFuture<Void> addAsync(T item);

    CompletableFuture<T> getByIdAsync(String id);

    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<Void> updateAsync(T currentItem, T newItem);

    CompletableFuture<Void> removeAsync(T item);

    CompletableFuture<String> uploadImageAsync(Uri imageUri, String imageName);

    CompletableFuture<Image> getImageAsync(String imageUri);
}
