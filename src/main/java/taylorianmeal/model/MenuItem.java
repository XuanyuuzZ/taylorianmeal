package taylorianmeal.model;

import javafx.beans.property.*;

/**
 * MenuItem
 * --------
 * Model class representing a menu item in a restaurant.
 */
public class MenuItem {
    private final IntegerProperty id;
    private final IntegerProperty restaurantId;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty price;
    private final StringProperty category;
    
    public MenuItem() {
        this.id = new SimpleIntegerProperty();
        this.restaurantId = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.category = new SimpleStringProperty();
    }
    
    public MenuItem(int id, int restaurantId, String name, String description, double price, String category) {
        this.id = new SimpleIntegerProperty(id);
        this.restaurantId = new SimpleIntegerProperty(restaurantId);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.category = new SimpleStringProperty(category);
    }
    
    // Getters
    public int getId() { return id.get(); }
    public int getRestaurantId() { return restaurantId.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public double getPrice() { return price.get(); }
    public String getCategory() { return category.get(); }
    
    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setRestaurantId(int restaurantId) { this.restaurantId.set(restaurantId); }
    public void setName(String name) { this.name.set(name); }
    public void setDescription(String description) { this.description.set(description); }
    public void setPrice(double price) { this.price.set(price); }
    public void setCategory(String category) { this.category.set(category); }
    
    // Properties for JavaFX binding
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty restaurantIdProperty() { return restaurantId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty priceProperty() { return price; }
    public StringProperty categoryProperty() { return category; }
    
    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + getId() +
                ", restaurantId=" + getRestaurantId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", price=" + getPrice() +
                ", category='" + getCategory() + '\'' +
                '}';
    }
} 