package taylorianmeal.util;

import taylorianmeal.model.User;

/**
 * SessionManager
 * --------------
 * Singleton class for managing user session information.
 * Replaces the original LoginSession class with better structure.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Set the current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Get the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Get the current user's ID
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    /**
     * Get the current user's username
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    /**
     * Get the current user's role
     */
    public taylorianmeal.model.UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Logout the current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Check if the current user has a specific role
     */
    public boolean hasRole(taylorianmeal.model.UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
    
    /**
     * Check if the current user is an admin
     */
    public boolean isAdmin() {
        return hasRole(taylorianmeal.model.UserRole.ADMIN);
    }
    
    /**
     * Check if the current user is a merchant
     */
    public boolean isMerchant() {
        return hasRole(taylorianmeal.model.UserRole.MERCHANT);
    }
    
    /**
     * Check if the current user is a customer
     */
    public boolean isCustomer() {
        return hasRole(taylorianmeal.model.UserRole.CUSTOMER);
    }
} 