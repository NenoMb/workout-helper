# Workout Backend

A Spring Boot application for workout tracking and management.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Deploying to GitHub](#deploying-to-github)

## Prerequisites
- Java 11 or higher
- Maven
- PostgreSQL
- Git

## Setup
1. Clone the repository (if you're not the original developer)
   ```
   git clone <repository-url>
   cd workout-backend
   ```

2. Create your application.properties file
   ```
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

3. Edit the application.properties file with your actual configuration values

## Configuration

### Environment Variables
The application uses environment variables for sensitive information. You can set these in your environment or use the default values for development:

#### Database Configuration
- `DB_URL` - Database URL (default: jdbc:postgresql://localhost:5432/workout_helper)
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

#### Email Configuration
- `MAIL_HOST` - SMTP host (default: smtp.gmail.com)
- `MAIL_PORT` - SMTP port (default: 587)
- `MAIL_USERNAME` - Email username
- `MAIL_PASSWORD` - Email password or app password

#### JWT Configuration
- `JWT_SECRET` - Secret key for JWT token generation
- `JWT_EXPIRATION` - Token expiration time in milliseconds (default: 86400000)

### Setting Environment Variables

#### Windows
```
set DB_USERNAME=your_username
set DB_PASSWORD=your_password
set JWT_SECRET=your_secret_key
```

#### Linux/Mac
```
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key
```

## Running the Application
```
mvn spring-boot:run
```

## Deploying to GitHub

### First-time Setup
1. Create a new repository on GitHub (don't initialize it with README, .gitignore, or license)

2. Initialize your local repository (if not already done)
   ```
   git init
   ```

3. Add your files to the repository
   ```
   git add .
   ```

4. Commit your changes
   ```
   git commit -m "Initial commit"
   ```

5. Add the remote repository
   ```
   git remote add origin https://github.com/yourusername/your-repo-name.git
   ```

6. Push your changes
   ```
   git push -u origin master
   ```

### Subsequent Pushes
```
git add .
git commit -m "Your commit message"
git push
```

### Important Security Notes
- Never commit sensitive information like passwords, API keys, or JWT secrets to your repository
- Always use environment variables or a secure configuration method for sensitive data
- The .gitignore file is configured to exclude application-dev.properties and application-prod.properties, which you can use for environment-specific configurations
- Consider using a secrets management tool for production deployments