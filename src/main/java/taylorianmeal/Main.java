package taylorianmeal;

import javafx.application.Application;
import javafx.stage.Stage;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;

import java.sql.*;

/**
 * Main
 * ----
 * Entry point for the TaylorianMeal JavaFX application.
 *
 * Startup sequence:
 *  1) Initialize/create database schema
 *  2) Ensure at least one Admin exists
 *  3) Seed demo data (restaurants + menu items)
 *  4) (DEBUG) Dump all users from the users table to console
 *  5) Store the primary Stage reference in Navigation
 *  6) Show the login page
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Create tables if they don't exist
        DatabaseManager.initDatabase();

        // 1-A. Ensure there's at least one Admin account
        //    (username: admin, password: Admin@123, full name: System Admin)
        DatabaseManager.ensureBuiltInAdmin(
                "admin",
                "Admin@123",
                "System Admin"
        );

        // 2. Seed initial demo data once
        DatabaseManager.seedInitialData();

        // 3. DEBUG: dump all users to the console
        dumpAllUsers();

        // 4. Save primary stage for Navigation
        Navigation.stage = primaryStage;

        // 5. Show the login page
        LoginPage.display();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }

    /**
     * DEBUG helper: reads every row in the users table and prints:
     *   id=…, user=…, role=…
     * to the console so you can verify that your merchant01 record
     * is visible to the application.
     */
    private static void dumpAllUsers() {
        final String url = DatabaseManager.JDBC_URL;
        final String sql = "SELECT id, username, role FROM users";

        System.out.println("── users table ──");
        try (Connection conn = DriverManager.getConnection(url);
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "id=%d, user=%s, role=%s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException ex) {
            System.err.println("Error dumping users: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

