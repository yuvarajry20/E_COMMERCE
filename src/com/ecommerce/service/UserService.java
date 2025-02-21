package com.ecommerce.service;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import java.util.List;

public class UserService {
    private UserDAO userDao;
    private OrderDAO orderDao;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public void registerUser(User user) {
        userDao.addUser(user);
    }

    public User loginUser(String username, String password) {
        User user = userDao.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }
}

