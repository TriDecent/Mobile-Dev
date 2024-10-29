package com.example.kiotz.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.example.kiotz.inventory.IInventory;
import com.example.kiotz.models.IIdentifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InventoryViewModel<T extends IIdentifiable> {
    private final IInventory<T> inventory;

    // fields for observing data in the View
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalQuantity = new MutableLiveData<>();

    public InventoryViewModel(IInventory<T> inventory) {
        this.inventory = inventory;
        loadInitialData(); // Load initial values when ViewModel is created
    }

    private void loadInitialData() {
        // Load total items and quantity asynchronously
        getTotalItemsAsync().thenAccept(totalItems::setValue);
        getTotalQuantity().thenAccept(totalQuantity::setValue);
    }

    // Public getters for LiveData
    public MutableLiveData<Integer> getTotalItemsLiveData() {
        loadInitialData();
        return totalItems;
    }

    public MutableLiveData<Integer> getTotalQuantityLiveData() {
        loadInitialData();
        return totalQuantity;
    }

    // Method to get the total count of items
    public CompletableFuture<Integer> getTotalItemsAsync() {
        return inventory.getTotalItemsAsync().thenApply(count -> {
            totalItems.postValue(count); // Update LiveData value
            return count;
        });
    }

    // Method to get the total quantity of all items
    public CompletableFuture<Integer> getTotalQuantity() {
        return inventory.getTotalQuantityAsync().thenApply(quantity -> {
            totalQuantity.postValue(quantity); // Update LiveData value
            return quantity;
        });
    }

    // Method to update the quantity of a specific item
    public CompletableFuture<Void> updateItemQuantity(T item, int newQuantity) {
        return inventory.updateItemQuantityAsync(item, newQuantity)
                .thenAccept(v -> getTotalQuantity()); // Refresh total quantity after update
    }

    // Method to increment the quantity of a specific item by a specified amount
    public CompletableFuture<Void> incrementQuantity(T item, int amount) {
        return inventory.incrementQuantityAsync(item, amount)
                .thenAccept(v -> getTotalQuantity()); // Refresh total quantity after increment
    }

    // Method to decrement the quantity of a specific item by a specified amount
    public CompletableFuture<Void> decrementQuantity(T item, int amount) {
        return inventory.decrementQuantityAsync(item, amount)
                .thenAccept(v -> getTotalQuantity()); // Refresh total quantity after decrement
    }

    // Method to add a new item to the inventory
    public CompletableFuture<Void> addItem(T item) {
        return inventory.addAsync(item)
                .thenAccept(v -> getTotalItemsAsync()); // Refresh total items after add
    }

    // Method to remove an item from the inventory by ID
    public CompletableFuture<Void> removeItem(int id) {
        return inventory.removeByIdAsync(id)
                .thenAccept(v -> getTotalItemsAsync()); // Refresh total items after remove
    }

    // Method to get an item from the inventory by ID
    public CompletableFuture<T> getItem(int id) {
        return inventory.getByIdAsync(id);
    }

    // Method to get all items from the inventory
    public CompletableFuture<List<T>> getAllItems() {
        return inventory.getAllAsync();
    }

    // Method to get the quantity of a specific item
    public CompletableFuture<Integer> getQuantity(T item) {
        return inventory.getQuantityByIdAsync(item.getID());
    }

    // Method to set the quantity of a specific item
    public CompletableFuture<Void> setQuantity(T item, int quantity) {
        return inventory.updateQuantityByIdAsync(item.getID(), quantity)
                .thenAccept(v -> {
                    getTotalQuantity(); // This will update totalQuantity LiveData
                });
    }
}


