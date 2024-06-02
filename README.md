# PSA Projects Management API

PSA Projects Management API is a RESTful service designed to manage projects, client relations, and resource allocations
within the PSA platform. This API is built using Spring Boot and integrates seamlessly with other components of the PSA
system to provide a robust and scalable backend solution for project management tasks.

## 🌟 Features

- **Project Management**: Create, update, delete, and retrieve project information.
- **Resource Allocation**: Assign resources to projects efficiently.
- **Client Relations**: Manage client data and link clients to projects.
- **Adoption Workflow**: Handle the adoption process for projects, integrating with the support service.

## 🛠️ Installation

To set up the PSA Projects Management API on your local environment, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/tribu-a-2024-1c/psa-projects
   cd psa-projects
   ```

2. Install dependencies and build the project:

   ```bash
   ./gradlew build
   ```

3. Start the Spring Boot application:

   ```bash
   ./gradlew bootRun
   ```

## ⚙️ Configuration

Configure environment variables in `application.properties` or via environment variables:

- `spring.datasource.url`: JDBC URL for the database connection.
- `spring.datasource.username`: Database username.
- `spring.datasource.password`: Database password.
- `support.api.ur`: URL to the Owner Service for project adoption workflows. Default is `http://localhost:8081`.

Example `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/projects
spring.datasource.username=${MYSQL_USER:projects}
spring.datasource.password=${MYSQL_PASSWORD:projects}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
support.api.url=${OWNERS_SERVICE_URL:http://localhost:8081}
```

## 📖 API Endpoints

### Projects

- **Get all projects**: `GET /api/projects`
- **Get a project by ID**: `GET /api/projects/{id}`
- **Create a new project**: `POST /api/projects`
- **Update a project**: `PUT /api/projects/{id}`
- **Delete a project**: `DELETE /api/projects/{id}`

### Example Requests

- **Create a Project**

  ```bash
  curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{"name": "New Project", "type": "Development"}'
  ```

## 🤝 Contributors

Contributions are welcome! 
