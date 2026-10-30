package taylorianmeal.model;

import javafx.beans.property.*;

/**
 * User
 * ----
 * Model class representing a user in the system.
 */
public class User {
    private final IntegerProperty id;
    private final StringProperty fullName;
    private final StringProperty email;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty telephone;
    private final ObjectProperty<UserRole> role;
    
    public User() {
        this.id = new SimpleIntegerProperty();
        this.fullName = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.telephone = new SimpleStringProperty();
        this.role = new SimpleObjectProperty<>();
    }
    
    public User(int id, String fullName, String email, String username, String password, String telephone, UserRole role) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.telephone = new SimpleStringProperty(telephone);
        this.role = new SimpleObjectProperty<>(role);
    }
    
    // Getters
    public int getId() { return id.get(); }
    public String getFullName() { return fullName.get(); }
    public String getEmail() { return email.get(); }
    public String getUsername() { return username.get(); }
    public String getPassword() { return password.get(); }
    public String getTelephone() { return telephone.get(); }
    public UserRole getRole() { return role.get(); }
    
    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public void setEmail(String email) { this.email.set(email); }
    public void setUsername(String username) { this.username.set(username); }
    public void setPassword(String password) { this.password.set(password); }
    public void setTelephone(String telephone) { this.telephone.set(telephone); }
    public void setRole(UserRole role) { this.role.set(role); }
    
    // Properties for JavaFX binding
    public IntegerProperty idProperty() { return id; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty emailProperty() { return email; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty telephoneProperty() { return telephone; }
    public ObjectProperty<UserRole> roleProperty() { return role; }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", role=" + getRole() +
                '}';
    }
} 