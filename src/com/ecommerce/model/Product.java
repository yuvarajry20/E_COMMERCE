package com.ecommerce.model;

public class Product {
    private int productId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int sellerId;
    public Product() {}

    public Product(String name, String description, double price, int stock, int sellerId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sellerId = sellerId;
    }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
}
