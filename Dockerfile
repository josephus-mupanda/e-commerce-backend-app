# Use Azul Zulu OpenJDK 21
FROM azul/zulu-openjdk-debian:21.0.2 AS builder

WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline -B

# Copy project and build JAR
COPY . .
RUN mvn clean package -DskipTests

# ----------- Runtime Image -----------
FROM azul/zulu-openjdk-debian:21.0.2

WORKDIR /app
COPY --from=builder /app/target/e-commerce-backend-app-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

