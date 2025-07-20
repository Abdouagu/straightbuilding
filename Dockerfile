FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean install

EXPOSE 8080

CMD ["java", "-jar", "target/construction-0.0.1-SNAPSHOT.jar"]
