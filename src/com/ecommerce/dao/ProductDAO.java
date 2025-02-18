package com.ecommerce.dao;

import com.ecommerce.model.Product;
import java.util.List;

public interface ProductDAO {
    
    void addProduct(Product product);

    Product getProductById(int productId);

    List<Product> getAllProducts();

    List<Product> getProductsBySeller(int sellerId);

    void updateProduct(Product product);

    void deleteProduct(int productId);
}
