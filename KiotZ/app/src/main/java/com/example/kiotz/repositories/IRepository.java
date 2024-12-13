package com.example.kiotz.repositories;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.kiotz.models.IIdentifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IRepository<T extends IIdentifiable> {
    CompletableFuture<Void> addAsync(T item);

    CompletableFuture<Void> removeAsync(T item);

    CompletableFuture<Void> updateAsync(T currentItem, T newItem);

    CompletableFuture<T> getByIdAsync(String id);

    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<String> uploadImageAsync(Uri imageUri, String imageName);

    CompletableFuture<Bitmap> getBitmapAsync(String imageUri);
}
