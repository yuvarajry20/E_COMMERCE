package com.ecommerce.dao;

import com.ecommerce.model.Order;
import com.ecommerce.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    private Connection connection;

    public OrderDAOImpl() {
        connection = DBUtil.getConnection();
    }

    
    public void addOrder(Order order) {
        String query = "INSERT INTO orders (customerId, productId, quantity, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, order.getCustomerId());
            stmt.setInt(2, order.getProductId());
            stmt.setInt(3, order.getQuantity());
            stmt.setString(4, order.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
    public Order getOrderById(int orderId) {
        String query = "SELECT * FROM orders WHERE orderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("orderId"));
                order.setCustomerId(rs.getInt("customerId"));
                order.setProductId(rs.getInt("productId"));
                order.setQuantity(rs.getInt("quantity"));
                order.setStatus(rs.getString("status"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

  
    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE customerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("orderId"));
                order.setCustomerId(rs.getInt("customerId"));
                order.setProductId(rs.getInt("productId"));
                order.setQuantity(rs.getInt("quantity"));
                order.setStatus(rs.getString("status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

  
    public List<Order> getOrdersBySeller(int sellerId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.* FROM orders o JOIN products p ON o.productId = p.productId WHERE p.sellerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("orderId"));
                order.setCustomerId(rs.getInt("customerId"));
                order.setProductId(rs.getInt("productId"));
                order.setQuantity(rs.getInt("quantity"));
                order.setStatus(rs.getString("status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

   
    public void updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE orderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}