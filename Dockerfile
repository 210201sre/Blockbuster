FROM maven:3.6.3-openjdk-11 as builder
# base image with jdk 11 and maven
# jdk 11 is needed for jacoco

# Copy pom.xml and source-code
COPY pom.xml pom.xml
COPY src/ src/

# Build the application
RUN mvn clean package

# Separate stage to save the resulting image size
FROM java:8 as runner
# Expose port 8080 for out web-app
EXPOSE 8080
# Copy the jar file from the previous stage
COPY --from=builder target/Blockbuster-0.0.1-SNAPSHOT.jar blockbuster.jar
# Runs our program when the container is created
ENTRYPOINT ["java", "-jar", "blockbuster.jar"]