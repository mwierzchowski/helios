FROM adoptopenjdk/openjdk13:alpine-slim
LABEL maintainer="Marcin Wierzchowski"
LABEL github="https://github.com/mwierzchowski/helios"
RUN addgroup -S helios && adduser -S helios -G helios
USER helios:helios
ARG APPLICATION=build/libs/helios-*.jar
WORKDIR /opt/helios
COPY ${APPLICATION} helios.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","helios.jar"]