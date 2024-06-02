# Builder stage
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /opt/app

# Copy only the necessary files to download dependencies first and leverage caching
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon || return 0

# Copy the rest of the source files and build the project
COPY src ./src
RUN ./gradlew build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /opt/app
EXPOSE 8080

# Copy the built jar from the builder stage
COPY --from=builder /opt/app/build/libs/*.jar /opt/app/app.jar

# Set the entry point
ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "/opt/app/app.jar"]
