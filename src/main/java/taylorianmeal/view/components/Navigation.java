package taylorianmeal.view.components;

import javafx.stage.Stage;
import taylorianmeal.view.pages.dashboard.StudentDashboard;
import taylorianmeal.view.pages.support.HelpDeskPage;

/**
 * Navigation
 * ----------
 * Provides static navigation methods for switching between different pages in the application.
 * Holds a reference to the primary Stage for global access.
 */
public class Navigation {
    public static Stage stage;

    public static void goToStudentDashboard() {
        StudentDashboard.display(); // Or whatever your method is called
    }

    public static void goToHelpDesk() {
        HelpDeskPage.display();
    }

}
