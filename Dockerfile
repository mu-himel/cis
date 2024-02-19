FROM openjdk:17-oracle

#ADD src/main/resources/db src/main/resources/db

#COPY THE JAR FILE
ADD target/cwh-vms-backend.jar app.jar

# Create a new user named "appuser"
RUN useradd -ms /bin/bash appuser

# Switch to the newly created user
USER appuser

# set the startup command to execute the jar
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app.jar"]