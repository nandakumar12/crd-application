FROM maven:3-openjdk-11 as build
WORKDIR /app
COPY pom.xml pom.xml
RUN mvn clean install
COPY ./ .
RUN mvn package

FROM openjdk:11
WORKDIR /app
COPY --from=build /app/target/CRD-0.0.1-SNAPSHOT.jar .
EXPOSE 8000
CMD ["java", "-jar","CRD-0.0.1-SNAPSHOT.jar"]