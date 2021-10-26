FROM navikt/java:11
ENV APP_NAME=fastlegerest

COPY build/libs/*.jar app.jar

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=remote"
