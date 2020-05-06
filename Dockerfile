FROM adoptopenjdk/openjdk13:latest
MAINTAINER Marcin Wierzchowski (https://github.com/mwierzchowski/helios)
WORKDIR /opt/helios
ARG APP=build/libs/helios-0.0.1-SNAPSHOT.jar
COPY ${APP} helios.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","helios.jar"]
