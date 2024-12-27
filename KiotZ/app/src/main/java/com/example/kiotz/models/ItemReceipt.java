package com.example.kiotz.models;

public class ItemReceipt {
    private String image;
    private String name;
    private String id;
    private double price;
    private int quantity;

    public ItemReceipt() {
    }

    public ItemReceipt(String image, String name, String id, double price, int quantity) {
        this.image = image;
        this.name = name;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
