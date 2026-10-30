# TaylorianMeal - Catering Management System

## Project Structure Optimization Description

This project has been structurally optimized, adopting a layered architecture design to improve code maintainability and scalability.

## New Project Structure

```
src/main/java/taylorianmeal/
├── Main.java                          # Application entry point
├── DatabaseManager.java               # Database manager (to be refactored into DAO classes)
├── LoginSession.java                  # Login session management
├── config/                            # Configuration classes
│   └── AppConfig.java                 # Application configuration constants
├── model/                             # Data model layer
│   ├── User.java                      # User model
│   ├── UserRole.java                  # User role enum
│   ├── Restaurant.java                # Restaurant model
│   ├── MenuItem.java                  # Menu item model
│   ├── Order.java                     # Order model
│   ├── Issue.java                     # Issue model
│   └── TechnicalIssue.java            # Technical issue model
├── dao/                               # Data access layer (to be implemented)
│   ├── UserDAO.java                   # User data access object
│   ├── RestaurantDAO.java             # Restaurant data access object
│   └── OrderDAO.java                  # Order data access object
├── service/                           # Service (business logic) layer
│   ├── OrderManager.java              # Order management service
│   ├── NotificationManager.java       # Notification management service
│   ├── ComplaintManager.java          # Complaint management service
│   └── TechnicalIssueManager.java     # Technical issue management service
├── controller/                        # Controller layer (to be implemented)
│   ├── LoginController.java           # Login controller
│   ├── DashboardController.java       # Dashboard controller
│   └── OrderController.java           # Order controller
├── view/                              # View layer
│   ├── pages/                         # Page classes
│   │   ├── auth/                      # Authentication related pages
│   │   │   ├── LoginPage.java         # Login page
│   │   │   └── RegisterPage.java      # Register page
│   │   ├── dashboard/                 # Dashboard pages
│   │   │   ├── StudentDashboard.java  # Student dashboard
│   │   │   ├── MerchantDashboard.java # Merchant dashboard
│   │   │   └── AdminDashboard.java    # Admin dashboard
│   │   ├── order/                     # Order related pages
│   │   │   ├── PreOrderPage.java      # Pre-order page
│   │   │   ├── OrderDetailsPage.java  # Order details page
│   │   │   ├── PaymentSuccessfulPage.java # Payment successful page
│   │   │   └── OrderStatusHistoryPage.java # Order status history page
│   │   ├── admin/                     # Admin pages
│   │   │   ├── AdminUserManagementPage.java # User management page
│   │   │   ├── AdminIssueManagementPage.java # Issue management page
│   │   │   ├── AdminSystemSettingsPage.java # System settings page
│   │   │   └── AdminUserProfilePage.java # User profile page
│   │   ├── support/                   # Support pages
│   │   │   ├── HelpDeskPage.java      # Help desk page
│   │   │   ├── FAQCenterPage.java     # FAQ center page
│   │   │   └── ChatBotPage.java       # Chatbot page
│   │   └── [Other page classes]       # Other functional pages
│   ├── components/                    # UI components
│   │   └── Navigation.java            # Navigation component
│   └── utils/                         # UI utility classes (to be implemented)
│       └── UIUtils.java               # UI utility methods
├── util/                              # Utility classes
│   ├── SessionManager.java            # Session manager
│   └── ValidationUtils.java           # Validation utility class
└── exception/                         # Exception classes
    ├── DatabaseException.java         # Database exception
    └── ValidationException.java       # Validation exception
```

```
src/main/resources/
├── images/                            # Image resources
│   ├── logos/                         # Logo images
│   ├── menu/                          # Menu images
│   └── icons/                         # Icon images
├── css/                               # Stylesheets
│   └── styles.css                     # Main stylesheet
└── faq.md                             # FAQ content
```

## Architecture Description

### Layered Architecture

1. **Model Layer**: Defines data structures and business entities
2. **Data Access Layer (DAO)**: Handles database operations
3. **Service Layer**: Implements business rules and logic
4. **Controller Layer**: Handles user interaction and page navigation
5. **View Layer**: User interface components

### Package Organization Principles

- **Group by Functionality**: Classes related to the same functionality are placed in the same package
- **Group by Layer**: Classes of different layers are placed in different packages
- **Clear Naming**: Package names clearly express the purpose of the classes they contain

## Migration Status

### Completed
- ✅ Created new package structure
- ✅ Created model classes (User, UserRole, Restaurant, MenuItem, Order, Issue, TechnicalIssue)
- ✅ Created utility classes (SessionManager, ValidationUtils)
- ✅ Created exception classes (DatabaseException, ValidationException)
- ✅ Created configuration class (AppConfig)
- ✅ Moved page classes to corresponding packages
- ✅ Moved resource files to resources directory
- ✅ Updated package declarations
- ✅ Created service layer classes (OrderManager, NotificationManager, ComplaintManager, TechnicalIssueManager)
- ✅ Created CSS stylesheet

### To Do
- 🔄 Refactor DatabaseManager into multiple DAO classes
- 🔄 Create service layer interfaces
- 🔄 Create controller classes
- 🔄 Update all import statements
- 🔄 Create configuration files
- 🔄 Update Main.java to use the new structure

## Next Steps

1. Refactor DatabaseManager into multiple specialized DAO classes
2. Create service layer interfaces to separate business logic
3. Create controller classes to handle user interaction
4. Update import statements in all classes
5. Create configuration files
6. Test all functionalities

## Usage Instructions

To run the project, please ensure:
1. Java 11 or higher is installed
2. JavaFX is installed
3. In the project root directory, run: `mvn clean compile exec:java`

## Pre-configured Login Accounts

The system comes with pre-configured accounts for testing:

### Administrator Account
- **Username:** `admin`
- **Password:** `Admin@123`
- **Full Name:** System Admin
- **Role:** Admin
- **Email:** admin@taylorianmeal.com
- **Permissions:** Can add new merchant and admin users, manage system settings, and handle all user issues

### Merchant Account (OldTown White Coffee)
- **Username:** `oldtown`
- **Password:** `password123`
- **Full Name:** OldTown Manager
- **Role:** Merchant
- **Email:** oldtown@taylorianmeal.com
- **Phone:** 012-3456789
- **Restaurant:** OldTown White Coffee (Traditional Malaysian coffee and local cuisine)

### Demo Menu Items (OldTown)
- White Coffee - RM 5.50 (Beverages)
- Nasi Lemak - RM 8.90 (Main Course)
- Black Tea - RM 4.50 (Beverages)
- Chicken Rice - RM 12.90 (Main Course)

**Note:** These accounts are automatically created when the application starts for the first time. You can use these credentials to test different user roles and functionalities.

## Contribution Guidelines

Please follow these coding standards:
- Use English comments
- Follow Java naming conventions
- Keep classes single-responsibility
- Add appropriate exception handling 