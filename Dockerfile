FROM gradle:8.13-jdk21 AS build
WORKDIR /893-back
COPY . .
RUN ./gradlew clean build -x test
FROM openjdk:21-jdk-slim
WORKDIR /893-back
COPY --from=build /893-back/build/libs/*.jar 893-back.jar
ENTRYPOINT ["java", "-jar", "/893-back/893-back.jar"]
EXPOSE 8080
