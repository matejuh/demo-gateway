FROM adoptopenjdk/openjdk11:centos as builder

WORKDIR .

ARG JAR_FILE=./build/libs/demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ./demo.jar

RUN set -x \
    && mkdir -p dependency \
    && cd dependency \
    && jar -xf ../*.jar \
    && true

FROM adoptopenjdk/openjdk11:centos-jre

LABEL image_name="Demo gateway"

RUN groupadd -g 1112 demo && \
    useradd -r -u 1112 -g demo demo
USER demo

VOLUME /tmp

WORKDIR /app

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/demo.jar

COPY --from=builder . .

