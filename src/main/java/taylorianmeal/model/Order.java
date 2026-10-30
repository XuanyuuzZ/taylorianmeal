package taylorianmeal.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Order
 * -----
 * Model class representing an order in the system.
 */
public class Order {
    private final IntegerProperty id;
    private final IntegerProperty customerId;
    private final IntegerProperty restaurantId;
    private final ObjectProperty<LocalDateTime> orderDate;
    private final StringProperty status;
    private final DoubleProperty totalAmount;
    
    public Order() {
        this.id = new SimpleIntegerProperty();
        this.customerId = new SimpleIntegerProperty();
        this.restaurantId = new SimpleIntegerProperty();
        this.orderDate = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.totalAmount = new SimpleDoubleProperty();
    }
    
    public Order(int id, int customerId, int restaurantId, LocalDateTime orderDate, String status, double totalAmount) {
        this.id = new SimpleIntegerProperty(id);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.restaurantId = new SimpleIntegerProperty(restaurantId);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.status = new SimpleStringProperty(status);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
    }
    
    // Getters
    public int getId() { return id.get(); }
    public int getCustomerId() { return customerId.get(); }
    public int getRestaurantId() { return restaurantId.get(); }
    public LocalDateTime getOrderDate() { return orderDate.get(); }
    public String getStatus() { return status.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    
    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setCustomerId(int customerId) { this.customerId.set(customerId); }
    public void setRestaurantId(int restaurantId) { this.restaurantId.set(restaurantId); }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate.set(orderDate); }
    public void setStatus(String status) { this.status.set(status); }
    public void setTotalAmount(double totalAmount) { this.totalAmount.set(totalAmount); }
    
    // Properties for JavaFX binding
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty customerIdProperty() { return customerId; }
    public IntegerProperty restaurantIdProperty() { return restaurantId; }
    public ObjectProperty<LocalDateTime> orderDateProperty() { return orderDate; }
    public StringProperty statusProperty() { return status; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", customerId=" + getCustomerId() +
                ", restaurantId=" + getRestaurantId() +
                ", orderDate=" + getOrderDate() +
                ", status='" + getStatus() + '\'' +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
} 