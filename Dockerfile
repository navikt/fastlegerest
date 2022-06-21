FROM gcr.io/distroless/java17
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"
ENV APP_NAME=fastlegerest
ENV TZ="Europe/Oslo"
EXPOSE 8080
USER nonroot
CMD [ "app.jar" ]
