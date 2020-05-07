FROM adoptopenjdk/openjdk13:latest
MAINTAINER Marcin Wierzchowski (https://github.com/mwierzchowski/helios)
WORKDIR /opt/helios
ARG VERSION=SNAPSHOT
ARG APP=build/libs/helios-${VERSION}.jar
COPY ${APP} helios.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","helios.jar"]
