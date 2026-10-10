
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8081

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar target/management-0.0.1-SNAPSHOT.jar"]
