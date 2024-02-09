package org.example;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.Scanner;

public class Main {
    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/project1";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    // JDBC variables for opening, closing and managing connection
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter first name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter email:");
        String email = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        // Register a new user
        registerUser(firstName, email, password);

        System.out.println("Enter email to login:");
        String loginEmail = scanner.nextLine();

        System.out.println("Enter password to login:");
        String loginPassword = scanner.nextLine();

        // Login a user
        loginUser(loginEmail, loginPassword);

        scanner.close();
    }

    // Register a new user
    public static void registerUser(String firstName, String email, String password) {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("User with this email already exists.");
            } else {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                preparedStatement = connection.prepareStatement("INSERT INTO users (firstName, email, password) VALUES (?, ?, ?)");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.executeUpdate();
                System.out.println("User successfully registered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Login a user
    public static void loginUser(String email, String password) {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPasswordFromDB = resultSet.getString("password");
                if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                    System.out.println("Login successful. Welcome, " + resultSet.getString("firstName") + "!");
                } else {
                    System.out.println("Incorrect email or password.");
                }
            } else {
                System.out.println("Incorrect email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}