# Society Admin: Comprehensive Housing Society Management System

Society Admin is a Spring Boot-based application that provides a complete digital solution for managing residential
housing societies. It streamlines administrative tasks, resident communications, and facility management through a
secure and scalable platform.

The application offers comprehensive society management features including user management, building administration,
billing and charges calculation, notice management, and resident services. It implements role-based access control with
JWT authentication to ensure secure access for administrators and syndics (building managers). The system integrates
with cloud storage services like Cloudinary for file management and includes notification capabilities through Twilio.

## Repository Structure

```
.
├── src/main/java/com/vunum/SocietyAdmin/     # Core application code
│   ├── Config/                               # Security and CORS configuration
│   ├── Controller/                           # REST API endpoints (Admin and Syndic)
│   ├── DTO/                                  # Data transfer objects
│   ├── Service/                              # Business logic and services
│   ├── Utilities/                            # Helper classes for JWT, file upload, etc.
│   ├── entity/                               # JPA entities for data model
│   └── repository/                           # Data access layer with JPA repositories
├── src/main/resources/                       # Application configuration
│   └── application.properties                # Core application settings
├── .github/workflows/                        # CI/CD pipeline configuration
└── pom.xml                                   # Maven project configuration
```

## Prerequisites

Before beginning the installation, ensure you have the following prerequisites:

1. System Requirements:
    - Minimum 4GB RAM
    - 10GB free disk space
    - Internet connection for downloading dependencies

2. Required Software:
    - Java Development Kit (JDK) 23
    - PostgreSQL 15 or higher
    - IntelliJ IDEA Community Edition
    - Maven 3.6.3 or higher
    - Git

3. Service Accounts:
    - Twilio account for SMS functionality
    - Cloudinary account for file storage
    - SMTP server access for email notifications

## Detailed Installation Guide

### 1. Install Java 23

1. Download OpenJDK 23:
    - Visit [Oracle's JDK download page](https://www.oracle.com/java/technologies/downloads/)
    - Select JDK 23 for your operating system

2. Install JDK:
    - Windows: Run the downloaded installer
    - MacOS: Run the .pkg installer
    - Linux:
      ```bash
      sudo apt update
      sudo apt install openjdk-23-jdk
      ```

3. Verify installation:
   ```bash
   java -version
   ```

### 2. Install PostgreSQL

1. Download PostgreSQL:
    - Visit [PostgreSQL Downloads](https://www.postgresql.org/download/)
    - Choose your operating system
    - Download PostgreSQL 15 or higher

2. Installation steps:
    - Windows:
        - Run the installer
        - Use default port 5432
        - Set password as 'postgres'
        - Complete the installation

    - MacOS:
      ```bash
      brew install postgresql@15
      brew services start postgresql@15
      ```

    - Linux:
      ```bash
      sudo apt update
      sudo apt install postgresql postgresql-contrib
      sudo systemctl start postgresql
      sudo systemctl enable postgresql
      ```

3. Configure PostgreSQL:
   ```bash
   sudo -u postgres psql
   ALTER USER postgres WITH PASSWORD 'postgres';
   CREATE DATABASE society_db;
   \q
   ```

### 3. Install IntelliJ IDEA

1. Download IntelliJ IDEA:
    - Visit [IntelliJ IDEA Download Page](https://www.jetbrains.com/idea/download/)
    - Download Community Edition

2. Install IntelliJ IDEA:
    - Windows: Run the installer
    - MacOS: Move to Applications folder
    - Linux: Extract and run `idea.sh`

### 4. Project Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd society-admin
   ```

2. Configure IntelliJ IDEA:
    - Open IntelliJ IDEA
    - Select "Open Project"
    - Navigate to cloned directory
    - Wait for project indexing
    - Enable Auto-Import for Maven projects when prompted
    - Switch to AdminAPI branch in VSersion Control Tab

3. Configure application properties:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

4. Edit application.properties:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/society_db
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

5. Setup Database:
    - Install DBeaver:
        - Download from [DBeaver Website](https://dbeaver.io/download/)
        - Install for your OS
    - Configure Database Connection:
        - Open DBeaver
        - Click "New Database Connection"
        - Select PostgreSQL
        - Enter credentials:
            - Host: localhost
            - Port: 5432
            - Database: society_db
            - Username: postgres
            - Password: postgres
        - Test connection and save

### 5. Build and Run

1. Build the project:
   ```bash
   ./mvnw clean install
   ```

2. Run in IntelliJ IDEA:
    - Locate `SocietyAdminApplication.java`
    - Right-click and select "Run"
    - Enable Annotation Processing if prompted

3. Verify installation:
    - Access application at `http://localhost:10000`
    - Test endpoints using provided PostMan Collection
    - https://api.postman.com/collections/37536377-0e147610-77ae-4323-bb29-31b3d9bdf456?access_key=PMAT-01JR84BQTZAWFX4GFGT92ESN18
    - Further Documentation can be accessed from the Collection
    - Postman API share does not support incremental updates, to see the latest addition to Collection, re-import the collection. 

