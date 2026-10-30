package taylorianmeal.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * TechnicalIssue
 * --------------
 * Model class representing a technical issue reported by a user.
 * Contains properties for id, user, type, description, and status.
 */
public class TechnicalIssue {
    private final StringProperty id;
    private final StringProperty user;
    private final StringProperty type;
    private final StringProperty description;
    private final StringProperty status;

    public TechnicalIssue(String id, String user, String type, String description, String status) {
        this.id = new SimpleStringProperty(id);
        this.user = new SimpleStringProperty(user);
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
    }

    public String getId() { return id.get(); }
    public String getUser() { return user.get(); }
    public String getType() { return type.get(); }
    public String getDescription() { return description.get(); }
    public String getStatus() { return status.get(); }

    public StringProperty idProperty() { return id; }
    public StringProperty userProperty() { return user; }
    public StringProperty typeProperty() { return type; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty statusProperty() { return status; }
}