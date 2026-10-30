package taylorianmeal.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Issue
 * -----
 * Model class representing a general issue or complaint in the system.
 * Contains properties for id, title, role, reporter, status, date, and description.
 */
public class Issue {
    private final StringProperty id;
    private final StringProperty title;
    private final StringProperty role;
    private final StringProperty reporter;
    private final StringProperty status;
    private final StringProperty date;
    private final StringProperty description;

    public Issue(String id, String title, String role, String reporter, String status, String date, String description) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.role = new SimpleStringProperty(role);
        this.reporter = new SimpleStringProperty(reporter);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
        this.description = new SimpleStringProperty(description);
    }

    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getRole() { return role.get(); }
    public String getReporter() { return reporter.get(); }
    public String getStatus() { return status.get(); }
    public String getDate() { return date.get(); }
    public String getDescription() { return description.get(); }

    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty roleProperty() { return role; }
    public StringProperty reporterProperty() { return reporter; }
    public StringProperty statusProperty() { return status; }
    public StringProperty dateProperty() { return date; }
    public StringProperty descriptionProperty() { return description; }
}