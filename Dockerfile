FROM gcr.io/distroless/java21
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
COPY build/libs/*.jar app.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app.jar"]
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"