FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY expense-tracker/pom.xml .
COPY . .
RUN mvn clean package
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/expense-tracker-target.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
