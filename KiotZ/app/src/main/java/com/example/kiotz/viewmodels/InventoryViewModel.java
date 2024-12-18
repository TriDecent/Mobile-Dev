package com.example.kiotz.viewmodels;

import android.media.Image;
import android.net.Uri;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.kiotz.inventory.IInventory;
import com.example.kiotz.models.IIdentifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InventoryViewModel<T extends IIdentifiable> {
    private final IInventory<T> inventory;

    private final MutableLiveData<List<T>> items = new MutableLiveData<>();
    private final MutableLiveData<T> addedItem = new MutableLiveData<>();
    private final MutableLiveData<Pair<Integer, T>> deletedItem = new MutableLiveData<>();
    private final MutableLiveData<Pair<Integer, T>> updatedItem = new MutableLiveData<>();

    public InventoryViewModel(IInventory<T> inventory) {
        this.inventory = inventory;
        loadItems();
    }

    public MutableLiveData<List<T>> getObservableItems() {
        return items;
    }

    public MutableLiveData<T> getObservableAddedItem() {
        return addedItem;
    }

    public MutableLiveData<Pair<Integer, T>> getObservableDeletedItem() {
        return deletedItem;
    }

    public MutableLiveData<Pair<Integer, T>> getObservableUpdatedItem() {
        return updatedItem;
    }

    public CompletableFuture<Void> add(T item) {
        return inventory.addAsync(item).thenRun(() -> {
            addedItem.postValue(item);
            addItemToObservableList(item);
        });
    }

    public CompletableFuture<Void> delete(T item) {
        return inventory.removeAsync(item).thenRun(() -> removeItemFromObservableList(item));
    }

    public CompletableFuture<Void> update(T currentItem, T newItem) {
        return inventory.updateAsync(currentItem, newItem).thenRun(() ->
                updateItemInObservableList(currentItem, newItem));
    }

    public CompletableFuture<List<T>> getAll() {
        return inventory.getAllAsync();
    }

    public CompletableFuture<T> getById(String id) {
        return inventory.getByIdAsync(id);
    }

    public CompletableFuture<String> uploadImage(Uri imageUri, String imageName) {
        return inventory.uploadImageAsync(imageUri, imageName);
    }

    public CompletableFuture<Image> getImage(String imageUri) {
        return inventory.getImageAsync(imageUri);
    }

    private void loadItems() {
        inventory.getAllAsync().thenAccept(items::postValue);
    }

    private void addItemToObservableList(T item) {
        List<T> currentList = items.getValue();
        if (currentList != null) {
            currentList.add(item);
            items.setValue(currentList);
        }
    }

    private void removeItemFromObservableList(T item) {
        List<T> currentList = items.getValue();
        if (currentList != null) {
            int position = currentList.indexOf(item);
            if (position != -1) {
                currentList.remove(position);

                deletedItem.setValue(new Pair<>(position, item));
                items.setValue(currentList);
            }
        }
    }

    private void updateItemInObservableList(T currentItem, T newItem) {
        List<T> currentList = items.getValue();
        if (currentList != null) {
            int position = currentList.indexOf(currentItem);
            if (position != -1) {
                currentList.set(position, newItem);

                updatedItem.setValue(new Pair<>(position, newItem));
                items.setValue(currentList);
            }
        }
    }
}