package taylorianmeal.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import taylorianmeal.DatabaseManager;

import java.sql.*;

/**
 * TechnicalIssueManager – CRUD utilities for `technical_issues` table.
 */
public final class TechnicalIssueManager {

    /** Row model for TableView or custom cards. */
    public record IssueRow(
            long   id,
            String type,
            String description,
            String status,
            String createdAt ) {}

    /** Insert a new tech-issue (called by the "Submit" button). */
    public static void add(int reporterId,
                           String type,
                           String description) throws SQLException {

        final String SQL = """
            INSERT INTO technical_issues (reporter_id, issue_type, description)
            VALUES (?,?,?)
        """;

        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL))
        {
            ps.setInt   (1, reporterId);
            ps.setString(2, type);
            ps.setString(3, description);
            ps.executeUpdate();
        }
    }

    /** List all issues of one reporter (merchant / customer). */
    public static ObservableList<IssueRow> listByReporter(int reporterId) throws SQLException {

        final String SQL = """
            SELECT id,
                   issue_type,
                   description,
                   status,
                   datetime(created_at,'localtime') AS ts
              FROM technical_issues
             WHERE reporter_id = ?
             ORDER BY created_at DESC
        """;

        ObservableList<IssueRow> list = FXCollections.observableArrayList();

        try (Connection c  = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL))
        {
            ps.setInt(1, reporterId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new IssueRow(
                            rs.getLong("id"),
                            rs.getString("issue_type"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getString("ts")));
                }
            }
        }
        return list;
    }

    private TechnicalIssueManager(){}           // no instance
}