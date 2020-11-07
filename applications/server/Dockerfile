FROM openjdk:jre-alpine
ENV MODE dev
ENV PROCESS genericAvro
RUN mkdir -p /opt/app
RUN mkdir -p /var/log/processes
WORKDIR /opt/app

COPY ./run_jar.sh ./target/scala-2.12/es-kafkatest-app-server.jar ./src/main/resources/application.conf ./src/main/resources/application.dev.conf ./src/main/resources/application.test.conf ./src/main/resources/application.prod.conf ./src/main/resources/logback.dev.xml ./src/main/resources/logback.test.xml ./src/main/resources/logback.prod.xml ./
#COPY ./run_jar.sh ./es-kafkatest-app-client.jar ./
#COPY ./run_jar.sh ./
#RUN chmod +x ./run_jar.sh
ENTRYPOINT ["./run_jar.sh"]