package com.example.kiotz.viewmodels;

import com.example.kiotz.inventory.IInventory;
import com.example.kiotz.models.IIdentifiable;

import java.util.HashMap;
import java.util.Map;

public class InventoryViewModelFactory {
    private static InventoryViewModelFactory instance;
    private final Map<Class<? extends IIdentifiable>, InventoryViewModel<? extends IIdentifiable>> instances = new HashMap<>();

    private InventoryViewModelFactory() {
        // Private constructor to prevent instantiation
    }

    public static synchronized InventoryViewModelFactory getInstance() {
        if (instance == null) {
            instance = new InventoryViewModelFactory();
        }
        return instance;
    }

    public synchronized <T extends IIdentifiable> InventoryViewModel<T> getViewModel(IInventory<T> inventory, Class<T> typeClass) {
        if (!instances.containsKey(typeClass)) {
            instances.put(typeClass, new InventoryViewModel<>(inventory));
        }
        return (InventoryViewModel<T>) instances.get(typeClass);
    }
}