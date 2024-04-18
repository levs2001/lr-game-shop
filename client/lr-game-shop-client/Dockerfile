# https://skillbox.ru/media/base/kak_rabotat_s_docker_upakovka_spring_boot_prilozheniya_v_konteyner/
FROM eclipse-temurin:21.0.2_13-jdk
ARG JAR_FILE=/target/lr-game-shop-client-1.0.0.jar
WORKDIR /opt/app
COPY ${JAR_FILE} lr-game-shop-client-app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "lr-game-shop-client-app.jar"]