package com.ecommerce.service;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import java.util.List;

public class OrderService {
    private OrderDAO orderDAO;

    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    
    public void placeOrder(Order order) {
        orderDAO.addOrder(order);
    }


    public Order getOrderById(int orderId) {
        return orderDAO.getOrderById(orderId);
    }

    
    public List<Order> getCustomerOrders(int customerId) {
        return orderDAO.getOrdersByCustomer(customerId);
    }

   
    public List<Order> getOrdersBySeller(int sellerId) {
        return orderDAO.getOrdersBySeller(sellerId);
    }

 
    public void updateOrderStatus(int orderId, String status) {
        orderDAO.updateOrderStatus(orderId, status);
    }
}