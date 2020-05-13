FROM adoptopenjdk/openjdk13:alpine-slim
LABEL maintainer="Marcin Wierzchowski"
LABEL github="https://github.com/mwierzchowski/helios"
ARG APP=build/libs/helios-*.jar
RUN addgroup -S helios && adduser -S helios -G helios
USER helios:helios
WORKDIR /opt/helios
COPY ${APP} helios.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","helios.jar"]