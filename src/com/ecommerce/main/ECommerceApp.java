package com.ecommerce.main;

import com.ecommerce.exception.*;
import com.ecommerce.model.User;
import com.ecommerce.model.Product;
import com.ecommerce.model.Order;
import com.ecommerce.service.UserService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.OrderService;
import com.ecommerce.dao.UserDAOImpl;
import com.ecommerce.dao.ProductDAOImpl;
import com.ecommerce.dao.OrderDAOImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class ECommerceApp {
    private static Connection connection;
    private static UserService userService;
    private static ProductService productService;
    private static OrderService orderService;
    private static User currentUser;

    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";


    public static void main(String[] args) {
    	 Scanner scanner = null;
         try {
             scanner = new Scanner(System.in);

             System.out.print("Enter database username: ");
             String dbUsername = scanner.nextLine();
             System.out.print("Enter database password: ");
             String dbPassword = scanner.nextLine();

             connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", dbUsername, dbPassword);
             connection.setAutoCommit(false);

             UserDAOImpl userDAO = new UserDAOImpl();
             ProductDAOImpl productDAO = new ProductDAOImpl();
             OrderDAOImpl orderDAO = new OrderDAOImpl();

             userService = new UserService(userDAO);
             productService = new ProductService(productDAO);
             orderService = new OrderService(orderDAO);

             boolean running = true;

             while (running) {
                 System.out.println("\n" + CYAN + "=== E-Commerce Platform ===" + RESET);
                 System.out.println(YELLOW + "1. Register" + RESET);
                 System.out.println(YELLOW + "2. Login" + RESET);
                 System.out.println(YELLOW + "3. Exit" + RESET);
                 System.out.print("Choose an option: ");
                 int choice = scanner.nextInt();
                 scanner.nextLine();

                 switch (choice) {
                     case 1:
                         registerUser(scanner);
                         break;
                     case 2:
                         try {
                             loginUser(scanner);
                             if (currentUser != null) {
                                 if (currentUser.getRole().equals("CUSTOMER")) {
                                     showCustomerMenu(scanner);
                                 } else if (currentUser.getRole().equals("SELLER")) {
                                     showSellerMenu(scanner);
                                 } else if (currentUser.getRole().equals("ADMIN")) {
                                     showAdminMenu(scanner);
                                 }
                             }
                         } catch (InvalidLoginException e) {
                             System.out.println(e);
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
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (scanner != null) {
                 scanner.close();
             }
             try {
                 if (connection != null) {
                     connection.close();
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
     }

    private static void registerUser(Scanner scanner) throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (CUSTOMER/SELLER/ADMIN): ");
        String role = scanner.nextLine().toUpperCase();

        String query = "INSERT INTO users (userId, username, password, role) VALUES (user_seq.NEXTVAL, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            connection.commit();
            System.out.println(YELLOW + "User registered successfully!" + RESET);
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void loginUser(Scanner scanner) throws SQLException , InvalidLoginException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUser = new User(rs.getInt("userId"), rs.getString("username"), rs.getString("password"), rs.getString("role"));
                System.out.println(YELLOW + "Login successful! Welcome, " + currentUser.getUsername() + RESET);
            } else {
                throw new InvalidLoginException (GREEN + "Invalid credentials!" + RESET);
            }
        }
    }

    private static void showCustomerMenu(Scanner scanner) throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n" + CYAN + "=== Customer Menu ===" + RESET);
            System.out.println(YELLOW + "1. Browse Products" + RESET);
            System.out.println(YELLOW + "2. Search Products" + RESET);
            System.out.println(YELLOW + "3. Add Product to Cart" + RESET);
            System.out.println(YELLOW + "4. View Cart" + RESET);
            System.out.println(YELLOW + "5. Checkout" + RESET);
            System.out.println(YELLOW + "6. View Orders" + RESET);
            System.out.println(YELLOW + "7. Logout" + RESET);
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    browseProducts();
                    break;
                case 2:
                    searchProducts(scanner);
                    break;
                case 3:
                    addProductToCart(scanner);
                    break;
                case 4:
                    viewCart();
                    break;
                case 5:
                    checkout();
                    break;
                case 6:
                    viewOrders();
                    break;
                case 7:
                    loggedIn = false;
                    currentUser = null;
                    System.out.println(YELLOW + "Logged out successfully!" + RESET);
                    break;
                default:
                    System.out.println(RED + "Invalid choice!" + RESET);
            }
        }
    }

    private static void browseProducts() throws SQLException {
        String query = "SELECT * FROM products";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n" + CYAN + "=== Available Products ===" + RESET);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("productId") + ", Name: " + rs.getString("name") + ", Price: $" + rs.getDouble("price") + ", Stock: " + rs.getInt("stock"));
            }
        }
    }

    private static void searchProducts(Scanner scanner) throws SQLException {
        System.out.println("\n" + CYAN + "=== Search Products ===" + RESET);
        System.out.println(YELLOW + "1. Search by Name" + RESET);
        System.out.println(YELLOW + "2. Search by Price Range" + RESET);
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                searchByName(scanner);
                break;
            case 2:
                searchByPriceRange(scanner);
                break;
            default:
                System.out.println(RED + "Invalid choice!" + RESET);
        }
    }

    private static void searchByName(Scanner scanner) throws SQLException {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();

        String query = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n" + CYAN + "=== Search Results ===" + RESET);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("productId") + ", Name: " + rs.getString("name") + ", Price: $" + rs.getDouble("price") + ", Stock: " + rs.getInt("stock"));
            }
        }
    }

    private static void searchByPriceRange(Scanner scanner) throws SQLException {
        System.out.print("Enter minimum price: ");
        double minPrice = scanner.nextDouble();
        System.out.print("Enter maximum price: ");
        double maxPrice = scanner.nextDouble();
        scanner.nextLine();

        String query = "SELECT * FROM products WHERE price BETWEEN ? AND ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n" + CYAN + "=== Search Results ===" + RESET);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("productId") + ", Name: " + rs.getString("name") + ", Price: $" + rs.getDouble("price") + ", Stock: " + rs.getInt("stock"));
            }
        }
    }

    private static void addProductToCart(Scanner scanner) throws SQLException {
        System.out.print("Enter product ID to add to cart: ");
        int productId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        String stockQuery = "SELECT stock, price FROM products WHERE productId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(stockQuery)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt("stock") >= quantity) {
                double totalPrice = rs.getDouble("price") * quantity;
                String orderQuery = "INSERT INTO orders (orderId, customerId, productId, quantity, status) VALUES (order_seq.NEXTVAL, ?, ?, ?, 'PENDING')";
                try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery)) {
                    orderStmt.setInt(1, currentUser.getUserId());
                    orderStmt.setInt(2, productId);
                    orderStmt.setInt(3, quantity);
                    orderStmt.executeUpdate();

                    String updateStock = "UPDATE products SET stock = stock - ? WHERE productId = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateStock)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, productId);
                        updateStmt.executeUpdate();
                    }
                    connection.commit();
                    System.out.println(YELLOW + "Product added to cart successfully!" + RESET);
                }
            } else {
                System.out.println(RED + "Invalid product ID or insufficient stock!" + RESET);
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void viewCart() throws SQLException {
        String query = "SELECT o.orderId, p.name, o.quantity, o.status FROM orders o JOIN products p ON o.productId = p.productId WHERE o.customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n" + CYAN + "=== Your Cart ===" + RESET);
            while (rs.next()) {
                System.out.println("Order ID: " + rs.getInt("orderId") + ", Product: " + rs.getString("name") + ", Quantity: " + rs.getInt("quantity") + ", Status: " + rs.getString("status"));
            }
        }
    }

    private static void checkout() throws SQLException {
        System.out.println("\n" + CYAN + "=== Checkout ===" + RESET);
        String query = "SELECT o.orderId, p.price, o.quantity FROM orders o JOIN products p ON o.productId = p.productId WHERE o.customerId = ? AND o.status = 'PENDING'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();
            double total = 0;

            while (rs.next()) {
                total += rs.getDouble("price") * rs.getInt("quantity");
                int orderId = rs.getInt("orderId");
                String updateStatus = "UPDATE orders SET status = 'SHIPPED' WHERE orderId = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateStatus)) {
                    updateStmt.setInt(1, orderId);
                    updateStmt.executeUpdate();
                }
            }

            System.out.println("Total amount: $" + total);
            System.out.println(YELLOW + "Order placed successfully!" + RESET);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void viewOrders() throws SQLException {
        String query = "SELECT o.orderId, p.name, o.quantity, o.status FROM orders o JOIN products p ON o.productId = p.productId WHERE o.customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n" + CYAN + "=== Your Orders ===" + RESET);
            while (rs.next()) {
                System.out.println("Order ID: " + rs.getInt("orderId") + ", Product: " + rs.getString("name") + ", Quantity: " + rs.getInt("quantity") + ", Status: " + rs.getString("status"));
            }
        }
    }

    private static void showSellerMenu(Scanner scanner) throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n" + CYAN + "=== Seller Menu ===" + RESET);
            System.out.println(YELLOW + "1. Add Product" + RESET);
            System.out.println(YELLOW + "2. Update Product" + RESET);
            System.out.println(YELLOW + "3. Delete Product" + RESET);
            System.out.println(YELLOW + "4. View My Products" + RESET);
            System.out.println(YELLOW + "5. Logout" + RESET);
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProduct(scanner);
                    break;
                case 2:
                    updateProduct(scanner);
                    break;
                case 3:
                    deleteProduct(scanner);
                    break;
                case 4:
                    viewMyProducts();
                    break;
                case 5:
                    loggedIn = false;
                    currentUser = null;
                    System.out.println(YELLOW + "Logged out successfully!" + RESET);
                    break;
                default:
                    System.out.println(RED + "Invalid choice!" + RESET);
            }
        }
    }

    private static void addProduct(Scanner scanner) throws SQLException {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product description: ");
        String description = scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter product stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        String query = "INSERT INTO products (productId, name, description, price, stock, sellerId) VALUES (product_seq.NEXTVAL, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, stock);
            pstmt.setInt(5, currentUser.getUserId());
            pstmt.executeUpdate();
            connection.commit();
            System.out.println(YELLOW + "Product added successfully!" + RESET);
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void updateProduct(Scanner scanner) throws SQLException {
        System.out.print("Enter product ID to update: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM products WHERE productId = ? AND sellerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.print("Enter new product name: ");
                String name = scanner.nextLine();
                System.out.print("Enter new product description: ");
                String description = scanner.nextLine();
                System.out.print("Enter new product price: ");
                double price = scanner.nextDouble();
                System.out.print("Enter new product stock: ");
                int stock = scanner.nextInt();
                scanner.nextLine();

                String updateQuery = "UPDATE products SET name = ?, description = ?, price = ?, stock = ? WHERE productId = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, name);
                    updateStmt.setString(2, description);
                    updateStmt.setDouble(3, price);
                    updateStmt.setInt(4, stock);
                    updateStmt.setInt(5, productId);
                    updateStmt.executeUpdate();
                    connection.commit();
                    System.out.println(YELLOW + "Product updated successfully!" + RESET);
                }
            } else {
                System.out.println(RED + "Invalid product ID or access denied!" + RESET);
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void deleteProduct(Scanner scanner) throws SQLException {
        System.out.print("Enter product ID to delete: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        String query = "DELETE FROM products WHERE productId = ? AND sellerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, currentUser.getUserId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                connection.commit();
                System.out.println(YELLOW + "Product deleted successfully!" + RESET);
            } else {
                System.out.println(RED + "Invalid product ID or access denied!" + RESET);
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void viewMyProducts() throws SQLException {
        String query = "SELECT * FROM products WHERE sellerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, currentUser.getUserId());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n" + CYAN + "=== My Products ===" + RESET);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("productId") + ", Name: " + rs.getString("name") +
                        ", Price: $" + rs.getDouble("price") + ", Stock: " + rs.getInt("stock"));
            }
        }
    }

    private static void showAdminMenu(Scanner scanner) throws SQLException {
        boolean adminLoggedIn = true;
        while (adminLoggedIn) {
            System.out.println("\n" + CYAN + "=== Admin Menu ===" + RESET);
            System.out.println(YELLOW + "1. View All Users" + RESET);
            System.out.println(YELLOW + "2. Delete User" + RESET);
            System.out.println(YELLOW + "3. View All Products" + RESET);
            System.out.println(YELLOW + "4. View All Orders" + RESET);
            System.out.println(YELLOW + "5. Logout" + RESET);
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    deleteUser(scanner);
                    break;
                case 3:
                    browseProducts();
                    break;
                case 4:
                    viewAllOrders();
                    break;
                case 5:
                    adminLoggedIn = false;
                    currentUser = null;
                    System.out.println(YELLOW + "Admin logged out successfully!" + RESET);
                    break;
                default:
                    System.out.println(RED + "Invalid choice!" + RESET);
            }
        }
    }

    private static void viewAllUsers() throws SQLException {
        String query = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n" + CYAN + "=== All Registered Users ===" + RESET);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("userId") + ", Username: " + rs.getString("username") + ", Role: " + rs.getString("role"));
            }
        }
    }

    private static void deleteUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user ID to delete: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        String query = "DELETE FROM users WHERE userId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                connection.commit();
                System.out.println(YELLOW + "User deleted successfully!" + RESET);
            } else {
                System.out.println(RED + "User not found!" + RESET);
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void viewAllOrders() throws SQLException {
        String query = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n" + CYAN + "=== All Orders ===" + RESET);
            while (rs.next()) {
                System.out.println("Order ID: " + rs.getInt("orderId") + ", Customer ID: " + rs.getInt("customerId") + ", Product ID: " + rs.getInt("productId") + ", Quantity: " + rs.getInt("quantity") + ", Status: " + rs.getString("status"));
            }
        }
    }
}