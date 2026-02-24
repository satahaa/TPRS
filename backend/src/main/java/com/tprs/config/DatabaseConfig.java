package com.tprs.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Configuration class for MySQL connection
 * Handles database connection pooling and management
 */
public class DatabaseConfig {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tprs_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "100503"; // Change this to your MySQL password
    
    private static Connection connection = null;
    
    /**
     * Get database connection (Singleton pattern with validation)
     * @return Connection object
     */
    public static synchronized Connection getConnection() {
        try {
            // Check if connection is null or closed/invalid
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✓ Database connection closed.");
            } catch (SQLException e) {
                System.err.println("✗ Error closing database connection!");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection test passed!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test failed!");
            e.printStackTrace();
        }
        return false;
    }
}
