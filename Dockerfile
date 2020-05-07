FROM adoptopenjdk/openjdk13:latest
MAINTAINER Marcin Wierzchowski (https://github.com/mwierzchowski/helios)
ARG VERSION=SNAPSHOT
ARG APPLICATION=build/libs/helios-${VERSION}.jar
WORKDIR /opt/helios
COPY ${APPLICATION} helios.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","helios.jar"]
