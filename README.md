# PSA Support Management API

PSA Support Management API is a RESTful service designed to manage support tickets, client interactions, and resource
allocations within the PSA platform. This API is built using Spring Boot and integrates seamlessly with other components
of the PSA system to provide a robust and scalable backend solution for support management tasks.

## 🌟 Features

- **Ticket Management**: Create, update, delete, and retrieve support ticket information.
- **Client Interactions**: Manage client data and link clients to support tickets.
- **Resource Allocation**: Assign resources to support tickets efficiently.

## 🛠️ Installation

To set up the PSA Support Management API on your local environment, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/tribu-a-2024-1c/psa-support
   cd psa-support
   ```

2. Create a docker network:

   ```bash
   docker network create psa-network
   ```

3. Install dependencies and build the project:

   ```bash
   ./gradlew build
   ```

4. Start the Spring Boot application:

   ```bash
   ./gradlew bootRun
   ```

## ⚙️ Configuration

Configure environment variables in `application.properties` or via environment variables:

- `spring.datasource.url`: JDBC URL for the database connection.
- `spring.datasource.username`: Database username.
- `spring.datasource.password`: Database password.
- `projects.api.url`: URL to the Projects Service for support workflows. Default is `http://localhost:8081`.

Example `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3307}/${MYSQL_DATABASE:support}
spring.datasource.username=${MYSQL_USER:support}
spring.datasource.password=${MYSQL_PASSWORD:support}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
projects.api.url=${API_PROJECTS_URL:http://localhost:8080}
```

## 📖 API Endpoints

### Tickets

- **Get all tickets**: `GET /tickets`
- **Get a ticket by ID**: `GET /tickets/{id}`
- **Create a new ticket**: `POST /tickets`
- **Update a ticket**: `PUT /tickets/{id}`
- **Delete a ticket**: `DELETE /tickets/{id}`

### Example Requests

- **Create a Ticket**

  ```bash
  curl -X POST http://localhost:8081/tickets \
  -H "Content-Type: application/json" \
  -d '{"title": "New Support Ticket", "description": "Issue details here"}'
  ```

## 🤝 Contributors

Contributions are welcome!


