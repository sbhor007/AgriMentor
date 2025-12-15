# Developer Setup Guide

## E-Consultancy for Farmers - Development Environment Setup

This guide will help you set up the development environment for the E-Consultancy for Farmers application.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [System Requirements](#system-requirements)
3. [Database Setup](#database-setup)
4. [Backend Setup](#backend-setup)
5. [Frontend Setup](#frontend-setup)
6. [Running the Application](#running-the-application)
7. [Development Workflow](#development-workflow)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

Install the following software before proceeding:

| Software | Version | Download Link |
|----------|---------|---------------|
| **Java JDK** | 21 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/) |
| **Node.js** | 18+ | [nodejs.org](https://nodejs.org/) |
| **npm** | 9+ | Comes with Node.js |
| **MySQL** | 8.0+ | [MySQL Downloads](https://dev.mysql.com/downloads/) |
| **Maven** | 3.6+ | [Maven Downloads](https://maven.apache.org/download.cgi) |
| **Git** | Latest | [git-scm.com](https://git-scm.com/) |

### Optional Tools

- **IDE**: IntelliJ IDEA (for backend) or Eclipse
- **Code Editor**: VS Code (for frontend)
- **API Testing**: Postman or Insomnia
- **Database Client**: MySQL Workbench or DBeaver

---

## System Requirements

### Minimum Requirements

- **OS**: Windows 10/11, macOS 10.15+, or Linux (Ubuntu 20.04+)
- **RAM**: 8 GB
- **Storage**: 5 GB free space
- **CPU**: Dual-core processor

### Recommended Requirements

- **RAM**: 16 GB
- **Storage**: 10 GB free space (SSD preferred)
- **CPU**: Quad-core processor

---

## Database Setup

### Step 1: Install MySQL

1. Download and install MySQL 8.0+ from the official website
2. During installation, set a root password (remember this!)
3. Start the MySQL service

### Step 2: Create Database

Open MySQL command line or MySQL Workbench and run:

```sql
-- Create database
CREATE DATABASE Efarming;

-- Verify database creation
SHOW DATABASES;

-- Use the database
USE Efarming;
```

### Step 3: Create Database User (Optional but Recommended)

```sql
-- Create a dedicated user for the application
CREATE USER 'efarming_user'@'localhost' IDENTIFIED BY 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON Efarming.* TO 'efarming_user'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

### Step 4: Verify Connection

```bash
mysql -u efarming_user -p Efarming
# Enter password when prompted
```

---

## Backend Setup

### Step 1: Clone Repository

```bash
git clone <repository-url>
cd E-Consultancy-for-Farmers/server
```

### Step 2: Configure Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Efarming
    username: efarming_user  # or 'root'
    password: your_secure_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update  # Creates tables automatically
    show-sql: true      # Shows SQL queries in console
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect

  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password  # Use App Password, not regular password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: 8080
  address: 0.0.0.0

jwt:
  secret-key: "your_very_long_and_secure_secret_key_here_minimum_256_bits"
```

### Step 3: Configure Email (Gmail)

To use Gmail for sending emails:

1. Go to [Google Account Settings](https://myaccount.google.com/)
2. Enable 2-Step Verification
3. Generate an App Password:
   - Go to Security → 2-Step Verification → App passwords
   - Select "Mail" and your device
   - Copy the generated 16-character password
4. Use this App Password in `application.yml`

### Step 4: Install Dependencies

```bash
mvn clean install
```

This will:
- Download all Maven dependencies
- Compile the project
- Run tests (if any)

### Step 5: Verify Setup

```bash
mvn spring-boot:run
```

If successful, you should see:
```
Started ServerApplication in X.XXX seconds
```

The backend will be available at `http://localhost:8080`

### Step 6: Test Backend

Open browser and navigate to:
```
http://localhost:8080/actuator/health
```

You should see:
```json
{"status":"UP"}
```

---

## Frontend Setup

### Step 1: Navigate to Client Directory

```bash
cd ../client
```

### Step 2: Install Dependencies

```bash
npm install
```

This will install all packages listed in `package.json`, including:
- Angular 20
- Tailwind CSS
- STOMP.js
- And all other dependencies

### Step 3: Configure Environment

Create or verify `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'http://localhost:8080/ws'
};
```

### Step 4: Verify Tailwind Configuration

Check `tailwind.config.js`:

```javascript
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### Step 5: Start Development Server

```bash
ng serve
```

Or for a specific port:
```bash
ng serve --port 4200
```

The application will be available at `http://localhost:4200`

### Step 6: Verify Frontend

Open browser and navigate to:
```
http://localhost:4200
```

You should see the landing page.

---

## Running the Application

### Development Mode

#### Option 1: Run Both Servers Separately

**Terminal 1 - Backend**:
```bash
cd server
mvn spring-boot:run
```

**Terminal 2 - Frontend**:
```bash
cd client
ng serve
```

#### Option 2: Using npm Scripts (if configured)

```bash
# In client directory
npm run start:all  # If script exists
```

### Production Build

#### Backend
```bash
cd server
mvn clean package
java -jar target/server-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd client
ng build --configuration production
```

Build artifacts will be in `dist/client/browser/`

---

## Development Workflow

### Daily Development Routine

1. **Start MySQL Service**
   ```bash
   # Linux/Mac
   sudo systemctl start mysql
   
   # Windows
   net start MySQL80
   ```

2. **Pull Latest Changes**
   ```bash
   git pull origin main
   ```

3. **Start Backend**
   ```bash
   cd server
   mvn spring-boot:run
   ```

4. **Start Frontend**
   ```bash
   cd client
   ng serve
   ```

5. **Open Application**
   - Frontend: `http://localhost:4200`
   - Backend API: `http://localhost:8080/api`

### Making Changes

#### Backend Changes

1. Edit Java files in `server/src/main/java/com/server/`
2. Spring Boot DevTools will auto-reload (if configured)
3. Or manually restart: `Ctrl+C` and `mvn spring-boot:run`

#### Frontend Changes

1. Edit TypeScript/HTML/CSS files in `client/src/app/`
2. Angular CLI will auto-reload in browser
3. Check browser console for errors

### Database Changes

#### View Tables
```sql
USE Efarming;
SHOW TABLES;
DESCRIBE user;  -- View table structure
```

#### Reset Database (Development Only)
```sql
DROP DATABASE Efarming;
CREATE DATABASE Efarming;
```

Then restart backend to recreate tables.

---

## Troubleshooting

### Common Issues

#### 1. Backend Won't Start

**Error**: `Cannot create PoolableConnectionFactory`

**Solution**: Check MySQL is running and credentials are correct
```bash
# Check MySQL status
sudo systemctl status mysql

# Test connection
mysql -u efarming_user -p
```

---

#### 2. Port Already in Use

**Error**: `Port 8080 is already in use`

**Solution**: Kill the process using the port
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

#### 3. Frontend Build Errors

**Error**: `Cannot find module '@angular/core'`

**Solution**: Reinstall dependencies
```bash
rm -rf node_modules package-lock.json
npm install
```

---

#### 4. CORS Errors

**Error**: `Access to XMLHttpRequest blocked by CORS policy`

**Solution**: Verify CORS configuration in backend
```java
// In WebConfig.java or SecurityConfig.java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
}
```

---

#### 5. WebSocket Connection Failed

**Error**: `WebSocket connection failed`

**Solution**: 
1. Verify backend is running
2. Check WebSocket URL in frontend: `ws://localhost:8080/ws`
3. Ensure JWT token is valid

---

#### 6. Email Not Sending

**Error**: `AuthenticationFailedException`

**Solution**:
1. Use Gmail App Password, not regular password
2. Enable "Less secure app access" (not recommended)
3. Check SMTP settings in `application.yml`

---

#### 7. Database Tables Not Created

**Error**: Tables don't exist after starting backend

**Solution**: Check `application.yml`:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Should be 'update' or 'create'
```

---

### Logging and Debugging

#### Backend Logs

View logs in console or check `logs/app.log`

Increase log level in `application.yml`:
```yaml
logging:
  level:
    com.server: DEBUG
    org.springframework.web: DEBUG
```

#### Frontend Logs

Open browser Developer Tools (F12) and check:
- **Console**: JavaScript errors
- **Network**: API requests/responses
- **Application**: Local storage, session storage

---

### Useful Commands

#### Maven
```bash
mvn clean                 # Clean build artifacts
mvn compile              # Compile source code
mvn test                 # Run tests
mvn package              # Create JAR file
mvn spring-boot:run      # Run application
```

#### Angular CLI
```bash
ng serve                 # Start dev server
ng build                 # Build for production
ng test                  # Run unit tests
ng generate component    # Generate component
ng lint                  # Lint code
```

#### MySQL
```bash
mysql -u root -p         # Login to MySQL
SHOW DATABASES;          # List databases
USE Efarming;            # Select database
SHOW TABLES;             # List tables
DESCRIBE table_name;     # Show table structure
```

---

## IDE Setup

### IntelliJ IDEA (Backend)

1. **Import Project**
   - File → Open → Select `server` folder
   - Import as Maven project

2. **Configure JDK**
   - File → Project Structure → Project SDK → Select JDK 21

3. **Install Lombok Plugin**
   - File → Settings → Plugins → Search "Lombok" → Install

4. **Enable Annotation Processing**
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

### VS Code (Frontend)

1. **Install Extensions**
   - Angular Language Service
   - ESLint
   - Prettier
   - Tailwind CSS IntelliSense

2. **Configure Settings**
   ```json
   {
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode"
   }
   ```

---

## Next Steps

After setup:

1. ✅ Create test accounts (farmer and consultant)
2. ✅ Test authentication flow
3. ✅ Create a consultation request
4. ✅ Test chat functionality
5. ✅ Explore all features

---

## Getting Help

- Check [PROJECT_DOCUMENTATION.md](./PROJECT_DOCUMENTATION.md) for architecture details
- Check [API_REFERENCE.md](./API_REFERENCE.md) for API documentation
- Review error logs in console
- Check browser developer tools

---

**Last Updated**: December 10, 2025
