FROM maven:3.8.2-jdk-11

WORKDIR /tmp

COPY . /tmp/obm-fhirapi

WORKDIR /tmp/obm-fhirapi

RUN mvn clean install

EXPOSE 443