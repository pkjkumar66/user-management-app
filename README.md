# user-management-app

This is a sample Spring Boot application for user management, including CRUD operations on the User entity. It incorporates Spring Data JPA for data persistence and Spring Security for basic authentication. The application also includes integration tests using MockMvc.


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

#### Option 1: JAR File

1. Clone the repository:

    ```bash
    git clone https://github.com/pkjkumar66/user-management-app.git
    ```

2. Navigate to the project directory:

    ```bash
    cd user-management-app
    ```

3. Create JAR file:

    ```bash
    ./mvnw package
    ```

4. Run the application:

    ```bash
    java -jar target/app-0.0.1-SNAPSHOT.jar
    ```

#### Option 2: Docker

Download Docker from [Docker website](https://www.docker.com/products/docker-desktop/).

#### Pull Docker Image

1. Pull the Docker image from DockerHub:

    ```bash
    docker pull pkjkumar66/user-management-app:latest
    ```

#### Run App using Docker Container

1. Run the user-management-app on your local machine using a Docker container:

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

[Postman Collection](https://documenter.getpostman.com/view/25669291/2s9YeLWoU9)

### User Roles: Authentication and Authorization
The application defines three user roles: USER and ADMIN. Each role has specific permissions to access different API endpoints.

- USER: Read-only access to user-related endpoints.
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
curl --location 'http://localhost:8080/api/v1/users/add' \
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
curl --location --request PUT 'localhost:8080/api/v1/users/update/1' \
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
curl --location 'http://localhost:8080/api/v1/users/all' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213'
```

### Get User by userId

```bash
curl --location 'http://localhost:8080/api/v1/users/1' \
--header 'Authorization: Basic YWRtaW46dGVzdDEyMw==' \
--header 'Cookie: JSESSIONID=0F22F030927774A7A84EA74753482213'
```

### Delete User by userId

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/users/delete/1' \
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
  - we have used plan-text as our password for this assignment however we can use different algo like BCrypt..
  - we will store password in `my_user` table by salting it, like 
    - salt: generate a random string
    - use any encryption algorithm and do h(password + salt), then store that password along with salt
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
