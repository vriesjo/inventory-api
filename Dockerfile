FROM adoptopenjdk/openjdk11

WORKDIR /app
COPY ./target/*.jar app.war

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.war"]

EXPOSE 9091
