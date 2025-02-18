package com.ecommerce.dao;

import com.ecommerce.model.Order;
import java.util.List;

public interface OrderDAO {
    void addOrder(Order order);
    Order getOrderById(int orderId);
    List<Order> getOrdersByCustomer(int customerId);
    List<Order> getOrdersBySeller(int sellerId);
    void updateOrderStatus(int orderId, String status);
}