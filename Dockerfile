FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app
COPY ./src /app/src
COPY pom.xml /app/
RUN mvn clean package

FROM openjdk:11
WORKDIR /app
COPY  --from=builder /app/target/cosmosMigrator.jar /app/app.jar
CMD [ "java","-jar","app.jar" ]