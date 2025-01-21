FROM gcr.io/distroless/java21
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
USER nonroot
CMD [ "app.jar" ]