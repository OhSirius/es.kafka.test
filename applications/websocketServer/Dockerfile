FROM openjdk:jre-alpine
RUN mkdir -p /opt/app
RUN mkdir -p /var/log/processes
WORKDIR /opt/app

COPY run_jar.sh ./target/scala-2.12/es-kafkatest-app-websocketserver.jar ./src/main/resources/application.conf ./src/main/resources/application.dev.conf ./src/main/resources/application.test.conf ./src/main/resources/application.prod.conf ./src/main/resources/logback.dev.xml ./src/main/resources/logback.test.xml ./src/main/resources/logback.prod.xml ./

RUN chmod +x run_jar.sh