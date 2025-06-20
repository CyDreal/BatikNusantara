package com.example.batiknusantara.model;

public class CartItem {
    private int id;
    private String productKode;
    private String productName;
    private Double price;
    private int quantity;
    private String imageUrl;
    private int stock;

    public CartItem(String productKode, String productName, Double price, int quantity, String imageUrl, int stock) {
        this.productKode = productKode;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    public Double getSubtotal() {
        return (price * quantity);
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getProductKode() { return productKode; }
    public String getProductName() { return productName; }
    public Double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getImageUrl() { return imageUrl; }
    public int getStock() { return stock; }
}
