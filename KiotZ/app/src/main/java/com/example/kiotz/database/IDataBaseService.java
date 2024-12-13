package com.example.kiotz.database;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDataBaseService<T> {
    CompletableFuture<Void> addAsync(T item);

    CompletableFuture<Void> removeAsync(T item);

    CompletableFuture<T> getByIdAsync(String id);

    CompletableFuture<List<T>> getAllAsync();

    CompletableFuture<Void> updateAsync(T currentItem, T newItem);

    CompletableFuture<String> uploadImageAsync(Uri imageUri, String imageName);

    CompletableFuture<Bitmap> getBitmapAsync(String imageUri);
}

