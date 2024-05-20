FROM maven:3.6.3-openjdk-17-slim as BUILDER

WORKDIR /app

COPY . .

COPY ./src/main/resources/application.prod.properties ./src/main/resources/application.properties

RUN mvn clean install -DskipTests


FROM openjdk:17-oracle as PROD

WORKDIR /app

COPY --from=BUILDER /app/target/*.jar app.jar

### couldnt upload to uploads folder due to specific user but not root

# RUN useradd -ms /bin/bash appuser
# USER appuser

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "./app.jar"]