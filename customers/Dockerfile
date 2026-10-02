FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY . .

RUN sed -i 's/\r$//' gradlew \
    && chmod +x gradlew \
    && ./gradlew --no-daemon bootJar

RUN set -eu; \
    jar_path="$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)"; \
    test -n "$jar_path"; \
    cp "$jar_path" /workspace/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build --chown=10001:10001 /workspace/app.jar /app/app.jar
USER 10001:10001

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
