# Use the Azul Zulu OpenJDK 21.0.2 image as the base
FROM azul/zulu-openjdk-debian:21.0.2

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download and cache the dependencies
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline -B

# Copy the entire project
COPY . .

# Build the application
RUN mvn package -DskipTests

# Expose the port your application runs on (replace 8080 if your app runs on a different port)
EXPOSE 8080

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "target/e-commerce-backend-app-0.0.1-SNAPSHOT.jar"]


## Use Azul Zulu OpenJDK 21
#FROM azul/zulu-openjdk-debian:21.0.2 AS builder
#
#WORKDIR /app
#
## Copy pom and download dependencies
#COPY pom.xml .
#RUN apt-get update && apt-get install -y maven
#RUN mvn dependency:go-offline -B
#
## Copy project and build JAR
#COPY . .
#RUN mvn clean package -DskipTests
#
## ----------- Runtime Image -----------
#FROM azul/zulu-openjdk-debian:21.0.2
#
#WORKDIR /app
#COPY --from=builder /app/target/e-commerce-backend-app-0.0.1-SNAPSHOT.jar ./app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
#
