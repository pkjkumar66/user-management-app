# Use the official OpenJDK base image with a specific Java version
FROM openjdk:17

# Set the working directory inside the container
RUN mkdir -p /home/app

# Copy the JAR file (your Java application) into the container
COPY target/app-0.0.1-SNAPSHOT.jar /home/app/application.jar

# Expose the port your application listens on (if applicable)
EXPOSE 8080

# Define the command to run your Java application
CMD ["java", "-jar", "/home/app/application.jar"]
