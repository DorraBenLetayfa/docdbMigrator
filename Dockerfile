FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app
COPY ./src /app/src
COPY pom.xml /app/
RUN mvn clean package

FROM openjdk:12-alpine
WORKDIR /app
# Install needed packages
RUN apk update \
        && apk upgrade \
        && apk --no-cache add curl openssl perl

# add truststore needed for connection to DocumentDB cluster
COPY import_rds_certs.sh /certs/import_rds_certs.sh
ARG trustStorePassword
ENV TRUST_STORE_PASSWORD=$trustStorePassword
RUN chmod +x /certs/import_rds_certs.sh
RUN /certs/import_rds_certs.sh $TRUST_STORE_PASSWORD
COPY --from=builder /app/target/cosmosMigrator.jar /app/app.jar
CMD java -Djavax.net.ssl.trustStore=/certs/rds-truststore.jks -Djavax.net.ssl.trustStorePassword=$TRUST_STORE_PASSWORD -jar app.jar                                                                                                                                                                                                                                                                                 
