FROM cgr.dev/chainguard/jre:latest
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
COPY build/libs/populasjonstilgangskontroll-1.0.0-plain.jar /app/app.jar
WORKDIR /app
CMD ["-jar", "app.jar"]

