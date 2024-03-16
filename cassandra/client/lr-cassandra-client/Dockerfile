# https://skillbox.ru/media/base/kak_rabotat_s_docker_upakovka_spring_boot_prilozheniya_v_konteyner/
FROM bellsoft/liberica-openjdk-alpine:17.0.3
ARG JAR_FILE=/target/lr-cassandra-client-1.0.0.jar
WORKDIR /opt/app
COPY ${JAR_FILE} cassandra-app.jar
ENTRYPOINT ["java", "-jar", "cassandra-app.jar"]