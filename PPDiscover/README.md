# PPDiscover

A Spring Boot web application for PPDiscover.

## Prerequisites

- Java 23 or higher
- Maven 3.6 or higher

## Building the Application

To build the application, run:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory.

## Running the Application

You can run the application in two ways:

1. Using Maven:
```bash
mvn spring-boot:run
```

2. Using the generated JAR file:
```bash
java -jar target/PPDiscover-1.0-SNAPSHOT.jar
```

The application will start on http://localhost:8080

## Project Structure

```
PPDiscover/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── ppdiscover/
│       │           ├── PPDiscoverApplication.java
│       │           └── controller/
│       │               └── HomeController.java
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```

## API Endpoints

- `GET /`: Welcome message
- `GET /actuator/health`: Application health status
- `GET /actuator/info`: Application information 