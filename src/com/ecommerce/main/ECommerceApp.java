package com.ecommerce.main;

import com.ecommerce.model.User;
import com.ecommerce.model.Product;
import com.ecommerce.model.Order;
import com.ecommerce.service.UserService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.OrderService;
import com.ecommerce.dao.UserDAOImpl;
import com.ecommerce.dao.ProductDAOImpl;
import com.ecommerce.dao.OrderDAOImpl;
import java.util.Scanner;
import java.util.List;

public class ECommerceApp {
    private static UserService userService;
    private static ProductService productService;
    private static OrderService orderService;
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
       
        UserDAOImpl userDAO = new UserDAOImpl();
        ProductDAOImpl productDAO = new ProductDAOImpl();
        OrderDAOImpl orderDAO = new OrderDAOImpl();

        userService = new UserService(userDAO);
        productService = new ProductService(productDAO);
        orderService = new OrderService(orderDAO);

        boolean running = true;

        while (running) {
            System.out.println("\n=== E-Commerce Platform ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    if (currentUser != null) {
                        showUserMenu();
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (CUSTOMER/SELLER/ADMIN): ");
        String role = scanner.nextLine().toUpperCase();

        User user = new User(username, password, role);
        userService.registerUser(user);
        System.out.println("User registered successfully!");
    }

    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        currentUser = userService.loginUser(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getUsername());
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private static void showUserMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Browse Products");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. View Orders");
            System.out.println("6. Manage Products (Seller Only)");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    browseProducts();
                    break;
                case 2:
                    addProductToCart();
                    break;
                case 3:
                    viewCart();
                    break;
                case 4:
                    checkout();
                    break;
                case 5:
                    viewOrders();
                    break;
                case 6:
                    if (currentUser.getRole().equals("SELLER")) {
                        System.out.println("Access approved! to be completed in next sprint.");
                    } else {
                        System.out.println("Access denied! Only sellers can manage products.");
                    }
                    break;
                case 7:
                    loggedIn = false;
                    currentUser = null;
                    System.out.println("Logged out successfully!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void browseProducts() {
        System.out.println("\n=== Available Products ===");
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            System.out.println("ID: " + product.getProductId() + ", Name: " + product.getName() +
                    ", Price: $" + product.getPrice() + ", Stock: " + product.getStock());
        }
    }

    private static void addProductToCart() {
        System.out.print("Enter product ID to add to cart: ");
        int productId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); 
        Product product = productService.getProductById(productId);
        if (product != null && product.getStock() >= quantity) {
            Order order = new Order(currentUser.getUserId(), productId, quantity, "PENDING");
            orderService.placeOrder(order);
            System.out.println("Product added to cart successfully!");
        } else {
            System.out.println("Invalid product ID or insufficient stock!");
        }
    }

    private static void viewCart() {
        System.out.println("\n=== Your Cart ===");
        List<Order> orders = orderService.getCustomerOrders(currentUser.getUserId());
        for (Order order : orders) {
            Product product = productService.getProductById(order.getProductId());
            System.out.println("Order ID: " + order.getOrderId() + ", Product: " + product.getName() +
                    ", Quantity: " + order.getQuantity() + ", Status: " + order.getStatus());
        }
    }

    private static void checkout() {
        System.out.println("\n=== Checkout ===");
        List<Order> orders = orderService.getCustomerOrders(currentUser.getUserId());
        double total = 0;

        for (Order order : orders) {
            Product product = productService.getProductById(order.getProductId());
            total += product.getPrice() * order.getQuantity();
            orderService.updateOrderStatus(order.getOrderId(), "SHIPPED");
        }

        System.out.println("Total amount: $" + total);
        System.out.println("Order placed successfully!");
    }

    private static void viewOrders() {
        System.out.println("\n=== Your Orders ===");
        List<Order> orders = orderService.getCustomerOrders(currentUser.getUserId());
        for (Order order : orders) {
            Product product = productService.getProductById(order.getProductId());
            System.out.println("Order ID: " + order.getOrderId() + ", Product: " + product.getName() +
                    ", Quantity: " + order.getQuantity() + ", Status: " + order.getStatus());
        }
    }
}