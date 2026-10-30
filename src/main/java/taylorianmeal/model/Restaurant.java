package taylorianmeal.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Restaurant
 * ----------
 * Model class representing a restaurant in the system.
 */
public class Restaurant {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty location;
    private final IntegerProperty merchantId;
    
    public Restaurant() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.merchantId = new SimpleIntegerProperty();
    }
    
    public Restaurant(int id, String name, String description, String location, int merchantId) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.merchantId = new SimpleIntegerProperty(merchantId);
    }
    
    // Getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public String getLocation() { return location.get(); }
    public int getMerchantId() { return merchantId.get(); }
    
    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setDescription(String description) { this.description.set(description); }
    public void setLocation(String location) { this.location.set(location); }
    public void setMerchantId(int merchantId) { this.merchantId.set(merchantId); }
    
    // Properties for JavaFX binding
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty locationProperty() { return location; }
    public IntegerProperty merchantIdProperty() { return merchantId; }
    
    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", location='" + getLocation() + '\'' +
                ", merchantId=" + getMerchantId() +
                '}';
    }
} 