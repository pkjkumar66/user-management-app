# user-management-app

This is a sample Spring Boot application for user management, including CRUD operations on the User entity. It incorporates Spring Data JPA for data persistence and Spring Security for basic authentication. The application follows RESTful conventions and includes integration tests using MockMvc.

```bash

- setup by step done in this repo: https://github.com/pkjkumar66/techolution-app
- I was facing some compilation issue while running through cmd line; java21 was not compactible with maven 3.2.0

```

## Table of Contents

- [Getting Started](#getting-started)
- [Features](#features)
- [Directory Structure](#directory)
- [Postman Collection](#postman-collection)
- [Endpoints](#endpoints)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Integration Tests](#integration-tests)
- [Exception Handling](#exception-handling)
- [Spring Security](#spring-security)

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven
- Your favorite IDE (IntelliJ, Eclipse, etc.)

### Installation

#### Option 1: Maven

1. Clone the repository:

    ```bash
    git clone https://github.com/pkjkumar66/user-management-app.git
    ```

2. Navigate to the project directory:

    ```bash
    cd user-management-app
    ```

3. Build the project:

    ```bash
    mvn clean install
    ```

4. Run the application:

    ```bash
    mvn spring-boot:run
    ```

#### Option 2: JAR File

1. Create JAR file:

    ```bash
    ./mvnw package
    ```

2. Run the application:

    ```bash
    java -jar target/app-0.0.1-SNAPSHOT.jar
    ```

#### Option 3: Docker

#### Pull Docker Image

1. Pull the Docker image from DockerHub:

    ```bash
    docker pull pkjkumar66/user-management-app:latest
    ```

#### Run App using Docker Container

1. Download Docker from [Docker website](https://www.docker.com/products/docker-desktop/).
2. Run the user-management-app on your local machine using a Docker container:

    ```bash
    docker run -p 8080:8080 pkjkumar66/user-management-app:latest
    ```

## Directory Structure

```bash
src
|-- main
|   |-- java
|       |-- com.example.app
|           |-- controller
|           |-- dto
|           |-- entity
|           |-- exception
|           |-- repository
|           |-- security
|           |-- service
|       |-- resources
|           |-- application.properties
|-- test
    |-- java
        |-- com.example.app
            |-- controller
```

## Features

- User entity with attributes such as id, username, and password.
- Spring Data JPA repository for user entity.
- RESTful's endpoints for CRUD operations on users.
- Spring Security configuration for basic authentication.
- Custom UserDetailsService to load user data from the database.
- Integration tests using MockMvc for authenticated and unauthenticated access.
- Global exception handling for meaningful error responses.
- Custom exceptions like ResourceNotFoundException, AccessDeniedException.



## Postman Collection

Explore and test the APIs using the provided Postman collection.

[Postman Collection](https://red-water-686645.postman.co/workspace/My-Workspace~bfb5c795-ecc4-4e23-8ad9-7c7fe4b847b4/collection/25669291-214b1db6-a12c-49ec-bbbe-7f43f2ca1cee?action=share&creator=25669291)

### User Roles: Authentication and Authorization
The application defines three user roles: EMPLOYEE, MANAGER, and ADMIN. Each role has specific permissions to access different API endpoints.

- EMPLOYEE: Read-only access to user-related endpoints.
- MANAGER: Read and write access to user-related endpoints.
- ADMIN: Full access to user-related endpoints.

Do authentication and authorization before using any APIs.
    ```bash
        username: admin
        password: test123
        role: ADMIN
    ```

## Endpoints

### Add user

```bash
curl --location 'http://localhost:8080/api/users' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213' \
--data '{
    "userName" : "pankaj",
    "password" : "12345"
}'
```

### Update existing user

```bash
curl --location --request PUT 'localhost:8080/api/users/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213' \
--data '{
"userName" : "pkjkumar",
"password" : "12345"
}'
```

### Get All the users

```bash
curl --location 'http://localhost:8080/api/users' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213'
```

### Get User by userId

```bash
curl --location 'http://localhost:8080/api/users/1' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213'
```

### Delete User by userId

```bash
curl --location --request DELETE 'http://localhost:8080/api/users/1' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213'
```

## Dependencies

- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Web
- H2 Database (Runtime)
- Spring Boot Starter Test
- Spring Security Test
- Lombok

## Configuration

- Spring Security Configuration for basic authentication.
- Custom UserDetailsService to load user data from the database.
- Global exception handling for various scenarios.
- Integration tests using MockMvc for different endpoints.

## Integration Tests

Integration tests are available in the `src/test` directory. These tests cover both authenticated and unauthenticated access to different endpoints, ensuring proper handling of various HTTP status codes.

To run the tests: `mvn test`

## Exception Handling

Custom exception handling is implemented to provide meaningful error responses for scenarios like ResourceNotFoundException, AccessDeniedException, etc. This ensures a better user experience and debugging process.

## Spring Security

Spring Security is configured to enable basic authentication. Users with different roles (EMPLOYEE, MANAGER, ADMIN) have different levels of access to API endpoints. Proper security constraints are applied to enforce authorization.
