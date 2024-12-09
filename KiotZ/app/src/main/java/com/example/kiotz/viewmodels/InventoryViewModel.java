package com.example.kiotz.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.example.kiotz.inventory.IInventory;
import com.example.kiotz.models.IIdentifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InventoryViewModel<T extends IIdentifiable> {
    private final IInventory<T> inventory;

    public final MutableLiveData<List<T>> items = new MutableLiveData<>();
    public final MutableLiveData<T> addedItem = new MutableLiveData<>();
    public final MutableLiveData<Integer> deletedPosition = new MutableLiveData<>();
    public final MutableLiveData<Integer> updatedPosition = new MutableLiveData<>();

    public InventoryViewModel(IInventory<T> inventory) {
        this.inventory = inventory;
        loadItems(); // Load initial list of items
    }

    public MutableLiveData<List<T>> getObservableItems() {
        return items;
    }

    public MutableLiveData<T> getObservableAddedItem() {
        return addedItem;
    }

    public MutableLiveData<Integer> getObservableDeletedItemPosition() {
        return deletedPosition;
    }

    public MutableLiveData<Integer> getObservableUpdatedItemPosition() {
        return updatedPosition;
    }

    // Load initial list of items
    public void loadItems() {
        inventory.getAllAsync().thenAccept(items::postValue);
    }

    public CompletableFuture<Void> add(T item) {
        return inventory.addAsync(item).thenRun(() -> {
            addedItem.postValue(item);
            addItemToList(item); // Add item to the observable list
        });
    }

    public CompletableFuture<Void> delete(T item) {
        return inventory.removeAsync(item).thenRun(() -> {
            removeItemFromList(item); // Remove item from the observable list
        });
    }

    public CompletableFuture<Void> update(T currentItem, T newItem) {
        return inventory.updateAsync(currentItem, newItem).thenRun(() -> {
            updateItemInList(currentItem, newItem); // Update item in the observable list
        });
    }

    // Asynchronous retrieval for UI compatibility
    public CompletableFuture<List<T>> getAll() {
        return inventory.getAllAsync();
    }

    public CompletableFuture<T> getById(String id) {
        return inventory.getByIdAsync(id);
    }

    // Update items list by adding a new item
    private void addItemToList(T item) {
        List<T> currentList = items.getValue();
        if (currentList != null) {
            currentList.add(item);
            items.setValue(currentList);
        }
    }

    // Update items list by removing an item
    private void removeItemFromList(T item) {
        List<T> currentList = items.getValue();
        if (currentList != null) {
            int position = currentList.indexOf(item);
            if (position != -1) {
                currentList.remove(position);
                deletedPosition.setValue(position); // Notify observers of the deleted item's position
                items.setValue(currentList);
            }
        }
    }

    // Update items list by modifying an existing item
    private void updateItemInList(T currentItem, T newItem) {
        List<T> currentList = items.getValue();
        if (currentList != null) {
            int position = currentList.indexOf(currentItem);
            if (position != -1) {
                currentList.set(position, newItem);
                updatedPosition.setValue(position); // Notify observers of the updated item's position
                items.setValue(currentList);
            }
        }
    }
}






