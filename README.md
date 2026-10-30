# TaylorianMeal

A JavaFX campus catering management application created as a university coursework project. It provides separate customer, merchant and administrator workflows for meal pre-ordering and campus food-service management.

## Features

- Role-based registration, login and dashboards.
- Menu browsing, pre-orders, order totals, status tracking and order history.
- Merchant menu and order management.
- Administrator user management, complaints and technical-issue workflows.
- Local SQLite persistence through JDBC.
- Optional AI customer-service chat using LangChain4j and OpenAI, plus optional SendGrid email notifications.

## Technology

Java, JavaFX 24.0.1, Maven, SQLite/JDBC, LangChain4j and SendGrid. Source targets Java 17; the bundled JavaFX 24 dependency requires JDK 22 or newer. JDK 23 was used for the publication build check.

## Run locally

Install JDK 22+ and Maven, then run from the project root:

```sh
mvn clean compile
mvn javafx:run
```

The application creates a local `taylorianmeal.db` and seeds demonstration accounts and menu items. No personal database is distributed.

| Role | Username | Demo password |
| --- | --- | --- |
| Administrator | `admin` | `Admin@123` |
| Merchant | `oldtown` | `password123` |

Create a student/customer account through registration. These credentials are for local demonstration only.

## Optional integrations

Set `OPENAI_API_KEY` in the launching terminal to enable the chatbot. Set `SENDGRID_API_KEY` and `SENDGRID_FROM_EMAIL` to enable email delivery with a sender you control. `.env.example` lists the variable names; the application does not load `.env` files automatically. Keep real credentials out of Git.

## Structure

- `src/main/java/taylorianmeal/model`: domain models.
- `src/main/java/taylorianmeal/service`: business services.
- `src/main/java/taylorianmeal/view`: JavaFX screens and shared components.
- `src/main/java/taylorianmeal/DatabaseManager.java`: schema and database operations.
- `src/main/resources`: application images, styles and FAQ material.
- `docs/ORIGINAL_PROJECT_NOTES.md`: original coursework structure notes.

## Project context

Published by Wu Xuanyu as a university coursework portfolio project from the supplied project archive. The archive contains no Git history, so this repository starts with a new import; it does not establish sole authorship. Existing source credits are retained. Third-party brand images belong to their respective owners.

This is an educational prototype. Password storage and seeded accounts are intended for local demonstration; payment-success screens do not establish real payment processing.
