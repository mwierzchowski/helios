FROM adoptopenjdk/openjdk13:alpine-slim
MAINTAINER Marcin Wierzchowski (https://github.com/mwierzchowski/helios)
ARG APPLICATION=build/libs/helios-*.jar
WORKDIR /opt/helios
COPY ${APPLICATION} helios.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","helios.jar"]
